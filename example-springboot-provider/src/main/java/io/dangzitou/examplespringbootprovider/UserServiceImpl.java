package io.dangzitou.examplespringbootprovider;

import io.dangzitou.example.common.model.User;
import io.dangzitou.example.common.service.UserService;
import io.dangzitou.zyrorpc.springboot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        log.info("User: {}", user);
        return user;
    }

    @Override
    public String getInfo(User user) {
        log.info("Received user info: {}", user);
        return String.format("Username: %s, Job: %s", user.getName(), user.getJob());
    }
}
