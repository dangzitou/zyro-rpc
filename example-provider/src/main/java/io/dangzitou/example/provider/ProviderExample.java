package io.dangzitou.example.provider;

import io.dangzitou.example.common.service.UserService;
import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.config.RegistryConfig;
import io.dangzitou.rpc.config.RpcConfig;
import io.dangzitou.rpc.model.ServiceMetaInfo;
import io.dangzitou.rpc.registry.LocalRegistry;
import io.dangzitou.rpc.registry.Registry;
import io.dangzitou.rpc.registry.RegistryFactory;
import io.dangzitou.rpc.server.HttpServer;
import io.dangzitou.rpc.server.VertxHttpServer;
import io.dangzitou.rpc.server.tcp.VertxTcpServer;

public class ProviderExample {
    /**
     * RPC框架提供者示例，展示如何使用RPC框架进行服务提供
     * @param args
     */
    public static void main(String[] args) {
        //RPC框架初始化
        RpcApplication.init();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        int port = rpcConfig.getServerPort();
        //如果命令行参数指定了端口号，则使用命令行参数，否则使用配置文件中的端口号
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        //注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        //注册服务到注册中心
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(port);
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //启动tcp服务
        VertxTcpServer tcpServer = new VertxTcpServer();
        tcpServer.doStart(port);

        //启动web服务
        /*HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());*/
    }
}
