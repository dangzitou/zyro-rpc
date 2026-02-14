package io.dangzitou.example.consumer;

import io.dangzitou.rpc.config.RpcConfig;
import io.dangzitou.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerExample {
    /**
     * RPC框架消费者示例，展示如何使用RPC框架进行远程调用
     * @param args
     */
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        log.info("rpc config: {}", rpc.toString());
    }
}
