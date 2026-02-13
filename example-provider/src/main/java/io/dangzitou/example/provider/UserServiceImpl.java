package io.dangzitou.example.provider;

import io.dangzitou.example.common.model.User;
import io.dangzitou.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    /**
     * 模拟获取用户信息的服务方法
     * @param user 用户对象
     * @return 返回传入的用户对象
     */
    @Override
    public User getUser(User user) {
        System.out.println("用户名:" + user.getName());
        return user;
    }
}
