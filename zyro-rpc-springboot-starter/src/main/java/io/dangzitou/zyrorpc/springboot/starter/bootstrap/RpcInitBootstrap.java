package io.dangzitou.zyrorpc.springboot.starter.bootstrap;

import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.config.RpcConfig;
import io.dangzitou.rpc.server.tcp.VertxTcpServer;
import io.dangzitou.zyrorpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //获取EnableRpc注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).
                get("needServer");

        //RPC框架初始化
        RpcApplication.init();

        //全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //启动服务器
        if(needServer){
            VertxTcpServer tcpServer = new VertxTcpServer();
            tcpServer.doStart(rpcConfig.getServerPort());
        }else{
            log.info("RPC client mode enabled, server will not be started.");
        }
    }
}
