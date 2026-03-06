package io.dangzitou.rpc.loadbalancer.impl;

import io.dangzitou.rpc.loadbalancer.LoadBalancer;
import io.dangzitou.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器实现类，按照顺序轮流选择服务实例，适用于服务实例性能相近的场景
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    /**
     * 轮询负载均衡器实现，使用AtomicInteger来记录当前的服务实例索引，每次选择时递增索引并取模服务实例列表的大小，以实现轮询效果
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        int size = serviceMetaInfoList.size();
        //如果只有一个服务实例，直接返回该实例
        if(size == 1){
            return serviceMetaInfoList.get(0);
        }
        //获取当前索引并递增，使用取模运算确保索引在服务实例列表的范围内，实现轮询效果
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}
