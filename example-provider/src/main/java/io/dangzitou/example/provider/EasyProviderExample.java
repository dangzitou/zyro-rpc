package io.dangzitou.example.provider;

import io.dangzitou.example.common.service.UserService;
import io.dangzitou.rpc.registry.LocalRegistry;
import io.dangzitou.rpc.server.HttpServer;
import io.dangzitou.rpc.server.VertxHttpServer;

/**
 * 简单的服务提供者示例
 * @author dangzitou
 * @date 2025/2/11
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        //启动web服务器
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8081);
    }
}
