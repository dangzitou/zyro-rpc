package io.dangzitou.rpc.loadbalancer;

import io.dangzitou.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器接口，定义了选择服务实例的方法
 */
public interface LoadBalancer {
    /**
     * 根据请求参数和可用的服务实例列表，选择一个服务实例进行调用
     * @param requestParams
     * @param serviceMetaInfoList
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
