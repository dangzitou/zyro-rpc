package io.dangzitou.zyrorpc.springboot.starter.bootstrap;

import io.dangzitou.rpc.proxy.ServiceProxyFactory;
import io.dangzitou.zyrorpc.springboot.starter.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;

public class RpcConsumerBootstrap implements BeanPostProcessor {
    @Override
    public @Nullable Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        //遍历对象的所有属性
        Field[] declaredFields = beanClass.getDeclaredFields();
        for(Field declaredField : declaredFields){
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);//获取@RpcReference注解
            if(rpcReference != null){
                //如果属性上有@RpcReference注解，则进行RPC代理对象的注入
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    //如果没有指定接口类，则默认使用属性的类型
                    interfaceClass = declaredField.getType();
                }

                declaredField.setAccessible(true);//设置属性可访问
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try{
                    declaredField.set(bean, proxyObject);//将代理对象注入到属性中
                    declaredField.setAccessible(false);//恢复属性的访问权限
                }catch (IllegalAccessException e){
                    throw new RuntimeException("Failed to inject RPC proxy object for field: " + declaredField.getName(), e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
