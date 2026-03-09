package io.dangzitou.example.provider;

import io.dangzitou.example.common.service.UserService;
import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.bootstrap.ProviderBootstrap;
import io.dangzitou.rpc.config.RegistryConfig;
import io.dangzitou.rpc.config.RpcConfig;
import io.dangzitou.rpc.model.ServiceMetaInfo;
import io.dangzitou.rpc.model.ServiceRegisterInfo;
import io.dangzitou.rpc.registry.LocalRegistry;
import io.dangzitou.rpc.registry.Registry;
import io.dangzitou.rpc.registry.RegistryFactory;
import io.dangzitou.rpc.server.HttpServer;
import io.dangzitou.rpc.server.VertxHttpServer;
import io.dangzitou.rpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {
    /**
     * RPC框架提供者示例，展示如何使用RPC框架进行服务提供
     * @param args
     */
    public static void main(String[] args) {
        List<ServiceRegisterInfo<?>> serviceRegisterInfos = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfos.add(serviceRegisterInfo);

        ProviderBootstrap.init(serviceRegisterInfos);
    }
}
