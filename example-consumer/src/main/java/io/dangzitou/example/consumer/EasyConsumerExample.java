package io.dangzitou.example.consumer;

import io.dangzitou.example.common.model.User;
import io.dangzitou.example.common.service.UserService;
import io.dangzitou.rpc.service.ServiceProxyFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EasyConsumerExample {
    public static void main(String[] args) {
        //调用提供者的服务
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("dangzitou");
        User result = userService.getUser(user);
        if (result != null) {
            log.info("获取用户信息成功，用户名: {}", result.getName());
        } else {
            log.info("获取用户信息失败");
        }
    }
}
