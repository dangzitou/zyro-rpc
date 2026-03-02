package io.dangzitou.rpc.registry.impl;

import io.dangzitou.rpc.config.RegistryConfig;
import io.dangzitou.rpc.model.ServiceMetaInfo;
import io.dangzitou.rpc.registry.Registry;
import io.dangzitou.rpc.registry.RegistryServiceCache;
import io.dangzitou.rpc.serializer.impl.JsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ZooKeeperRegistry implements Registry {
    /**
     * CuratorFramework客户端实例，用于与ZooKeeper服务器进行通信和操作
     */
    private CuratorFramework client;

    /**
     * ServiceDiscovery实例，用于执行服务注册、查询和监听等操作，提供了更高层次的抽象，简化了与ZooKeeper的交互逻辑
     */
    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;

    /**
     * 本机注册的节点key集合，用于心跳检测时判断哪些节点需要续约，确保只有本机注册的节点会被续约，避免误续约其他服务实例的节点
     */
    private final Set<String> localRegisterNodeKeys = new HashSet<>();

    /**
     * 服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的服务节点key集合
     */
    private final Set<String> watchingKeySet = new HashSet<>();

    /**
     * ZooKeeper根节点路径
     */
    private static final String ZOOKEEPER_ROOT_PATH = "/zyro-rpc/zookeeper";

    @Override
    public void init(RegistryConfig registryConfig) {
        //构建client实例
        client = CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getAddress())//连接字符串，指定ZooKeeper服务器的地址和端口
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))//重试策略，使用指数退避算法，初始等待时间为1000毫秒，最大重试次数为3次
                .sessionTimeoutMs(registryConfig.getTimeout().intValue())//会话超时时间，单位为毫秒
                .connectionTimeoutMs(registryConfig.getTimeout().intValue())//连接超时时间，单位为毫秒
                .build();
        //构建serviceDiscovery实例，指定服务元信息的类型为ServiceMetaInfo，并设置根节点路径
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZOOKEEPER_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();
        try{
            client.start();
            serviceDiscovery.start();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //注册到zk
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));
        //将注册的节点key添加到本地缓存
        String registryKey = ZOOKEEPER_ROOT_PATH + "/" + serviceMetaInfo.getServiceName();
        localRegisterNodeKeys.add(registryKey);
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        try{
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //从本地缓存中移除节点key
        String registerKe = ZOOKEEPER_ROOT_PATH + "/" + serviceMetaInfo.getServiceName();
        localRegisterNodeKeys.remove(registerKe);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceName) {
        //优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfos = registryServiceCache.readCache();
        if(cachedServiceMetaInfos != null && !cachedServiceMetaInfos.isEmpty()){
            return cachedServiceMetaInfos;
        }
        //从zk获取服务列表
        try {
            List<ServiceMetaInfo> serviceMetaInfos = serviceDiscovery.queryForInstances(serviceName).stream()
                    .map(ServiceInstance::getPayload)
                    .toList();
            //将服务列表写入缓存
            registryServiceCache.writeCache(serviceMetaInfos);
            return serviceMetaInfos;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void heartbeat() {
        //ZooKeeper的心跳机制由CuratorFramework客户端自动处理，无需手动实现心跳逻辑
    }

    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZOOKEEPER_ROOT_PATH + "/" + serviceNodeKey;
        boolean newWatch = watchingKeySet.add(watchKey);
        if(newWatch){
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener
                            .builder()
                            .forCreates(node -> log.info("节点创建: {}", node.getPath()))
                            .forChanges((oldNode, newNode) -> log.info("节点更新: {} -> {}", oldNode.getPath(), newNode.getPath()))
                            .forDeletes(node -> log.info("节点删除: {}", node.getPath()))
                            .build()
            );
        }
    }

    @Override
    public void destroy() {
        log.info("关闭ZooKeeper注册中心连接");
        //下线节点（这一步可以不用做，因为ZooKeeper会自动删除会话相关的临时节点，但为了确保节点被及时删除，可以手动删除注册的节点）
        for(String nodeKey : localRegisterNodeKeys){
            try {
                client.delete().guaranteed().forPath(nodeKey);//删除节点，guaranteed()方法确保即使发生连接问题也会继续尝试删除节点，直到成功为止
            } catch (Exception e) {
                log.error("注销服务失败，nodeKey={}", nodeKey, e);
            }
        }

        //释放资源
        if(client != null){
            client.close();
        }
    }

    /**
     * 构建ServiceInstance对象，将ServiceMetaInfo对象转换为ServiceInstance对象，包含服务名称、地址和负载信息等
     * @param serviceMetaInfo
     * @return
     */
    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
        try{
            return ServiceInstance
                    .<ServiceMetaInfo>builder()
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("构建ServiceInstance失败", e);
        }
    }
}
