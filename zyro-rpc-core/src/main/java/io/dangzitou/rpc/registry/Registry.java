package io.dangzitou.rpc.registry;

import io.dangzitou.rpc.config.RegistryConfig;
import io.dangzitou.rpc.model.ServiceMetaInfo;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 服务注册中心接口，定义了服务注册和发现的基本功能
 * @author dangzitou
 * @date 2026/02/18
 */
public interface Registry {
    /**
     * 注册服务，将服务信息注册到注册中心
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务，将服务信息注册到注册中心
     * @param serviceMetaInfo
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务，将服务信息从注册中心移除
     * @param serviceMetaInfo
     * @throws Exception
     */
    void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 服务发现，根据服务名称从注册中心获取服务信息列表
     * @param serviceName
     * @return
     * @throws IOException
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceName);

    /**
    * 监听服务节点的变化，当服务节点发生变化时，触发相应的处理逻辑
    * @param serviceNodeKey
    */
    void watch(String serviceNodeKey);

    /**
     * 心跳检测(服务端）
     */
    void heartbeat();

    /**
     * 销毁注册中心连接，释放资源
     */
    void destroy();
}
