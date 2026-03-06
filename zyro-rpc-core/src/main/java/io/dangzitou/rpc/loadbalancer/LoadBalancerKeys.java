package io.dangzitou.rpc.loadbalancer;

/**
 * 负载均衡器类型的常量定义接口，包含了不同负载均衡算法的标识字符串
 */
public interface LoadBalancerKeys {
    //轮询
    String ROUND_ROBIN = "roundRobin";
    //随机
    String RANDOM = "random";
    //一致性哈希
    String CONSISTENT_HASH = "consistentHash";
}
