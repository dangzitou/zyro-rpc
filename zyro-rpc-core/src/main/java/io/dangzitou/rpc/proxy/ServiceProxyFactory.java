package io.dangzitou.rpc.proxy;

import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.config.RpcConfig;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂类(ServiceProxyFactory)，用于创建服务代理对象
 * @author dangzitou
 * @date 2025/2/11
 */
public class ServiceProxyFactory {
    public static <T> T getProxy(Class<T> serviceClass) {
        //如果RPC框架处于模拟模式，返回一个MockServiceProxy的代理对象
        if(RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }
        //否则返回一个ServiceProxy的代理对象
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                new ServiceProxy()
        );
    }

    private static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                new MockServiceProxy()
        );
    }
}
