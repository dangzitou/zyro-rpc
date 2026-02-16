package io.dangzitou.example.common.service;

import io.dangzitou.example.common.model.User;

/**
 * 用户服务类，提供获取用户信息的方法
 */
public interface UserService {
    /**
     * 模拟获取用户信息的服务方法
     * @param user 用户对象
     * @return 返回传入的用户对象
     */
    User getUser(User user);

    /**
     * 新方法 - 获取str，测试mock
     */
    default String getStr() {
        return "default string";
    }
}
