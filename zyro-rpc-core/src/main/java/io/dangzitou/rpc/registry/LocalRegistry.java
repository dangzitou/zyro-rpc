package io.dangzitou.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 * @author dangzitou
 * @date 2025/2/11
 */
public class LocalRegistry {
    /**
     * 注册信息存储
     * 使用ConcurrentHashMap保证线程安全（多线程环境下注册和获取服务不会发生冲突）
     */
    private static final Map<String, Class<?>> servers = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serviceName 服务名称
     * @param serviceClass 服务实现类
     */
    public static void register(String serviceName, Class<?> serviceClass) {
        servers.put(serviceName, serviceClass);
    }

    /**
     * 获取服务实现类
     * @param serviceName 服务名称
     * @return 服务实现类
     */
    public static Class<?> getService(String serviceName) {
        return servers.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName 服务名称
     */
    public static void unregister(String serviceName) {
        servers.remove(serviceName);
    }
}
