package io.dangzitou.rpc.bootstrap;

import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.config.RegistryConfig;
import io.dangzitou.rpc.config.RpcConfig;
import io.dangzitou.rpc.model.ServiceMetaInfo;
import io.dangzitou.rpc.model.ServiceRegisterInfo;
import io.dangzitou.rpc.registry.LocalRegistry;
import io.dangzitou.rpc.registry.Registry;
import io.dangzitou.rpc.registry.RegistryFactory;
import io.dangzitou.rpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * RPC框架提供者引导类，负责初始化RPC框架、注册服务以及启动TCP服务器
 */
public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        //RPC框架初始化
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        for(ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            //注册服务
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            //注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "register fail", e);
            }
        }


        //启动tcp服务
        VertxTcpServer tcpServer = new VertxTcpServer();
        tcpServer.doStart(rpcConfig.getServerPort());
    }
}
