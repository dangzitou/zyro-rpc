package io.dangzitou.rpc.loadbalancer;

import io.dangzitou.rpc.loadbalancer.impl.RoundRobinLoadBalancer;
import io.dangzitou.rpc.spi.SpiLoader;

/**
 * 负载均衡器工厂类，提供获取负载均衡器实例的方法
 */
public class LoadBalancerFactory {
    static{
        SpiLoader.load(LoadBalancer.class);
    }
    //默认负载均衡器实例，使用轮询算法
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取负载均衡器实例
     * @param loadBalancerName
     * @return
     */
    public static LoadBalancer getInstance(String loadBalancerName){
        return SpiLoader.getInstance(LoadBalancer.class, loadBalancerName);
    }
}
