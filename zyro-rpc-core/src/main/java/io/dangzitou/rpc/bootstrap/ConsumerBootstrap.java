package io.dangzitou.rpc.bootstrap;

import io.dangzitou.rpc.RpcApplication;

/**
 * RPC框架消费者引导类，负责初始化RPC框架
 */
public class ConsumerBootstrap {
    public static void init() {
        //RPC框架初始化
        RpcApplication.init();
    }
}
