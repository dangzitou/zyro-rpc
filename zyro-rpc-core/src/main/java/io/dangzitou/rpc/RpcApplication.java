package io.dangzitou.rpc;

import io.dangzitou.rpc.config.RegistryConfig;
import io.dangzitou.rpc.config.RpcConfig;
import io.dangzitou.rpc.constant.RpcConstant;
import io.dangzitou.rpc.registry.Registry;
import io.dangzitou.rpc.registry.RegistryFactory;
import io.dangzitou.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用类，提供RPC框架的初始化和配置管理功能
 * @author dangzitou
 * @date 2025/02/13
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 获取RPC配置
     * @return RPC配置对象
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config:{}", rpcConfig.toString());
        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("rpc registry init, config:{}", registryConfig.toString());

        //创建并注册Shutdown Hook，在JVM关闭时执行清理操作，如注销服务等
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化RPC配置，从配置文件中加载RPC相关的配置项，如果加载失败则使用默认配置
     * @return RPC配置对象
     */
    public static void init() {
        RpcConfig newRpcConfig = new RpcConfig();
        try {
            //从配置文件中加载RPC配置
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            //加载失败则使用默认配置
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取RPC配置，如果RPC配置尚未初始化，则先进行初始化
     * @return RPC配置对象
     */
    public static RpcConfig getRpcConfig() {
        if(rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if(rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
