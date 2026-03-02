package io.dangzitou.rpc.registry;

import io.dangzitou.rpc.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache {
    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写入服务缓存
     * @param serviceMetaInfos
     */
    public void writeCache(List<ServiceMetaInfo> serviceMetaInfos) {
        this.serviceCache = serviceMetaInfos;
    }

    /**
     * 读取服务缓存
     * @return
     */
    public List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    /**
     * 清除服务缓存
     */
    public void clearCache() {
        this.serviceCache = null;
    }
}
