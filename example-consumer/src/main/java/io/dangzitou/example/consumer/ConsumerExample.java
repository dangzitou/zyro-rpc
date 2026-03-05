package io.dangzitou.example.consumer;

import io.dangzitou.example.common.model.User;
import io.dangzitou.example.common.service.UserService;
import io.dangzitou.rpc.config.RpcConfig;
import io.dangzitou.rpc.proxy.ServiceProxyFactory;
import io.dangzitou.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerExample {
    /**
     * RPC框架消费者示例，展示如何使用RPC框架进行远程调用
     * @param args
     */
    public static void main(String[] args) {
        /*RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        log.info("rpc config: {}", rpc.toString());*/
        for (int i = 0; i < 3; i++) {
            //调用提供者的服务
            UserService userService = ServiceProxyFactory.getProxy(UserService.class);
            User user = new User();
            user.setName("dangzitou");
            user.setJob("java");
            //调用
            String result = userService.getInfo(user);
            if (result != null) {
                log.info("Provider return: {}", result);
            } else {
                log.info("Get information failed.");
            }
            /*String str = userService.getStr();
            log.info("获取字符串: {}", str);*/
        }
    }
}
