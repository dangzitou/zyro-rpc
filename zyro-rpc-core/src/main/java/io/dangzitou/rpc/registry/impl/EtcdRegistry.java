package io.dangzitou.rpc.registry.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import io.dangzitou.rpc.config.RegistryConfig;
import io.dangzitou.rpc.model.ServiceMetaInfo;
import io.dangzitou.rpc.registry.Registry;
import io.dangzitou.rpc.registry.RegistryServiceCache;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class EtcdRegistry implements Registry {
    /**
     * Etcd客户端实例，用于与Etcd服务器进行通信和操作
     */
    private Client client;

    /**
     * Etcd的KV客户端实例，用于执行键值对相关的操作，如注册服务、查询服务等
     */
    private KV kvClient;

    /**
     * 本机注册的节点key集合，用于心跳检测时判断哪些节点需要续约
     */
    private final Set<String> localRegisterNodeKeys = new HashSet<>();

    /**
     * 服务缓存，用于存储从Etcd中查询到的服务信息，避免频繁访问Etcd服务器，提高性能
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的服务节点key集合，用于避免重复监听同一个节点，确保每个节点只被监听一次
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/zyro-rpc/etcd/";

    /**
     * 初始化Etcd客户端，连接到Etcd服务器，并启动心跳检测机制
     * @param registryConfig
     */
    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).build();//连接到Etcd服务器
        kvClient = client.getKVClient();//获取KV客户端实例
        heartbeat();//启动心跳检测机制，保持注册信息的有效性
    }

    /**
     * 注册服务，将服务信息注册到Etcd中，并关联一个租约，使得当租约过期时，注册信息会自动被删除
     * @param serviceMetaInfo
     * @throws Exception
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //创建lease和kv客户端
        Lease leaseClient = client.getLeaseClient();
        //创建一个租约，设置租约的TTL（Time To Live）为30
        long leaseId = leaseClient.grant(30).get().getID();
        //构建服务注册的键值对，键为服务名称，值为服务元信息的JSON字符串
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
        //将服务注册信息写入Etcd，并关联租约，使得当租约过期时，注册信息会自动被删除
        PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        //添加节点信息到本地缓存
        localRegisterNodeKeys.add(registryKey);
    }

    /**
     * 注销服务，从Etcd中删除对应的注册信息，并从本地缓存中移除节点信息
     * @param serviceMetaInfo
     * @throws Exception
     */
    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        log.info("unregister service:{}",serviceMetaInfo.getServiceNodeKey());
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        kvClient.delete(key).get();
        //从本地缓存中移除节点信息
        localRegisterNodeKeys.remove(registryKey);
    }

    /**
     * 服务发现，根据服务名称从Etcd中查询匹配的注册信息，并将其转换为ServiceMetaInfo对象列表返回
     * @param serviceKey
     * @return
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //优先从服务缓存中读取服务信息，如果缓存中存在且不为空，则直接返回缓存中的服务列表，避免频繁访问Etcd服务器，提高性能
        List<ServiceMetaInfo> cachedServiceList = registryServiceCache.readCache();
        if (CollUtil.isNotEmpty(cachedServiceList)) {
            log.info("从服务缓存中读取服务列表，serviceKey={}, serviceList={}", serviceKey, cachedServiceList);
            return cachedServiceList;
        }

        //构建查询前缀，查询以服务名称开头的所有注册信息
        String registryKeyPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        ByteSequence keyPrefix = ByteSequence.from(registryKeyPrefix, StandardCharsets.UTF_8);
        //从Etcd中获取匹配的键值对列表，并将其转换为ServiceMetaInfo对象列表返回
        try{
            //前缀查询
            log.info("从Etcd中查询服务列表，serviceKey={}", serviceKey);
            List<ServiceMetaInfo> serviceMetaInfoList = kvClient.get(keyPrefix, GetOption.newBuilder().withPrefix(keyPrefix).build())
                    .get()
                    .getKvs()
                    .stream()
                    .map(kv -> {
                        String valueStr = kv.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(valueStr, ServiceMetaInfo.class);
                    })
                    .toList();
            //监听服务节点的变化，当服务节点发生变化时，触发相应的处理逻辑
            watch(registryKeyPrefix);
            //写入服务缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    /**
     * 监听服务节点的变化，当服务节点发生变化时，触发相应的处理逻辑
     * @param serviceNodeKey
     */
    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if(newWatch){
            WatchOption watchOption = WatchOption.builder()
                    .withPrefix(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8))
                    .build();
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), watchOption, response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch(event.getEventType()){
                        case PUT:
                            /*String key = event.getKeyValue().getKey().toString(StandardCharsets.UTF_8);
                            String value = event.getKeyValue().getValue().toString(StandardCharsets.UTF_8);
                            log.info("服务节点新增或更新，key={}, value={}", key, value);*/
                            break;
                        case DELETE:
                            String key = event.getKeyValue().getKey().toString(StandardCharsets.UTF_8);
                            log.info("服务节点删除，key={}", key);
                            registryServiceCache.clearCache();
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void destroy() {
        log.info("当前节点下线，正在销毁Etcd连接...");
        //注销服务，从Etcd中删除对应的注册信息，并从本地缓存中移除节点信息
        for(String key : localRegisterNodeKeys){
            try {
                ByteSequence keyByte = ByteSequence.from(key, StandardCharsets.UTF_8);
                kvClient.delete(keyByte).get();
            } catch (Exception e) {
                log.error("注销服务失败，registryKey={}", key, e);
            }
        }
        //释放资源
        if (client != null) {
            client.close();
        }
        if (kvClient != null) {
            kvClient.close();
        }
    }

    /**
     * 心跳检测，使用定时任务每10秒续签一次租约，保持注册信息的有效性
     */
    @Override
    public void heartbeat() {
        //10秒续签一次租约，保持注册信息的有效性
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                //遍历本节点所有的key
                for (String registryKey : localRegisterNodeKeys) {
                    try {
                       List<KeyValue> keyValues = kvClient.get(ByteSequence.from(registryKey, StandardCharsets.UTF_8))
                               .get()
                               .getKvs();
                       if(CollUtil.isEmpty(keyValues)){
                           //节点已过期或被删除，需重启节点
                           continue;
                       }
                       //节点未过期
                       KeyValue keyValue = keyValues.get(0);
                       String valueStr = keyValue.getValue().toString(StandardCharsets.UTF_8);
                       ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(valueStr, ServiceMetaInfo.class);
                       //重新注册服务，续约租约
                       register(serviceMetaInfo);
                    } catch (Exception e) {
                        log.error("续约租约失败，registryKey={}", registryKey, e);
                    }
                }
            }
        });
        //设置匹配秒，确保任务每10秒执行一次
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }


}
