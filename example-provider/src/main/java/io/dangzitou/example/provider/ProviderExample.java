package io.dangzitou.example.provider;

import io.dangzitou.example.common.service.UserService;
import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.registry.LocalRegistry;
import io.dangzitou.rpc.server.HttpServer;
import io.dangzitou.rpc.server.VertxHttpServer;

public class ProviderExample {
    /**
     * RPC框架提供者示例，展示如何使用RPC框架进行服务提供
     * @param args
     */
    public static void main(String[] args) {
        //RPC框架初始化
        RpcApplication.init();

        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
