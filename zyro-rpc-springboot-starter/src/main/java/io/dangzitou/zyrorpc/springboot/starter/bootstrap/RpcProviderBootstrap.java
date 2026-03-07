package io.dangzitou.zyrorpc.springboot.starter.bootstrap;

import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.config.RegistryConfig;
import io.dangzitou.rpc.config.RpcConfig;
import io.dangzitou.rpc.model.ServiceMetaInfo;
import io.dangzitou.rpc.registry.LocalRegistry;
import io.dangzitou.rpc.registry.Registry;
import io.dangzitou.rpc.registry.RegistryFactory;
import io.dangzitou.zyrorpc.springboot.starter.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

public class RpcProviderBootstrap implements BeanPostProcessor {
    /**
     * 扫描带有@RpcService注解的Bean，并将其注册到本地服务注册中心
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public @Nullable Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);//获取@RpcService注解
        if (rpcService != null) {
            Class<?> interfaceClass = rpcService.interfaceClass();
            //如果没有指定接口类，则默认使用第一个接口
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }

            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            //本地注册
            LocalRegistry.register(serviceName, beanClass);

            //全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            //服务注册到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try{
                registry.register(serviceMetaInfo);
            }catch (Exception e){
                throw new RuntimeException("Failed to register service to registry center: " + serviceName, e);
            }
        }
        //如果没有@RpcService注解，则直接返回原始Bean
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
