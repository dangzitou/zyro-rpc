package io.dangzitou.rpc.registry;

import io.dangzitou.rpc.registry.impl.EtcdRegistry;
import io.dangzitou.rpc.spi.SpiLoader;

/**
 * 注册中心工厂类，用于创建不同类型的注册中心实例
 * @author dangzitou
 * @date 2026/02/18
 */
public class RegistryFactory {
    /**
     * 静态代码块，在类加载时执行，使用SpiLoader加载Registry接口的实现类
     */
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取默认注册中心实例
     * @param key
     * @return 默认注册中心实例
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
