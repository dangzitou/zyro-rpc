package io.dangzitou.rpc.config;

import io.dangzitou.rpc.loadbalancer.LoadBalancerKeys;
import io.dangzitou.rpc.serializer.SerializerKeys;
import lombok.Data;

import static io.dangzitou.rpc.fault.retry.RetryStrategyKeys.NO_RETRY;
import static io.dangzitou.rpc.fault.tolerant.TolerantStrategyKeys.FAIL_FAST;
import static io.dangzitou.rpc.serializer.SerializerKeys.JDK;

/**
 * RPC配置类，包含RPC相关的配置信息
 * @author dangzitou
 * @date 2025/02/13
 */
@Data
public class RpcConfig {
    /**
     * RPC服务的名称，默认为"zyro-rpc"
     */
    private String name = "zyro-rpc";

    /**
     * RPC服务的版本，默认为"1.0.0"
     */
    private String version = "1.0.0";

    /**
     * 服务器的主机地址，默认为"localhost"
     */
    private String serverHost = "localhost";

    /**
     * 服务器的端口，默认为8081
     */
    private Integer serverPort = 8081;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器，默认为"jdk"，可选值包括"json"、"kryo"、"hessian"
     */
    private String serializer = JDK;

    /**
     * 负载均衡器，默认为"roundRobin"，可选值包括"roundRobin"、"random"、"consistentHash"
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略，默认为"noRetry"，可选值包括"noRetry"、"fixedInterval"
     */
    private String retryStrategy = NO_RETRY;

    /**
     * 容错策略，默认为"failFast"，可选值包括"failFast"、"failOver"
     */
    private String tolerantStrategy = FAIL_FAST;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}
