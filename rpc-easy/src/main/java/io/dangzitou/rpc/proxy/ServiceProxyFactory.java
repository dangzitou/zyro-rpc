package io.dangzitou.rpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂类(ServiceProxyFactory)，用于创建服务代理对象
 * @author dangzitou
 * @date 2025/2/11
 */
public class ServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                new ServiceProxy()
        );
    }
}
