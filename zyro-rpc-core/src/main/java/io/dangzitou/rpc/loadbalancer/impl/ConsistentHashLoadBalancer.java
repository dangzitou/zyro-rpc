package io.dangzitou.rpc.loadbalancer.impl;

import io.dangzitou.rpc.loadbalancer.LoadBalancer;
import io.dangzitou.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器实现类
 * 基于一致性哈希算法选择服务实例
 * 适用于服务实例数量变化较频繁的场景
 * 可以减少请求在服务实例之间的迁移，提高系统的稳定性和性能
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {
    //一致性hash环，使用TreeMap来存储虚拟节点，key为虚拟节点的hash值，value为对应的服务实例信息
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();
    //虚拟节点数量，每个服务实例对应多个虚拟节点，以提高负载均衡的效果
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }

        //构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                String virtualNodeKey = serviceMetaInfo.getServiceAddress() + "#" + i;
                int hash = virtualNodeKey.hashCode();
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        //根据请求参数计算hash值
        int hash = requestParams.hashCode();
        //使用TreeMap的ceilingEntry方法找到第一个大于等于hash值的虚拟节点，如果没有找到，则返回第一个虚拟节点，实现环形效果
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }
}
