package io.dangzitou.rpc.config;

import lombok.Data;

/**
 * RPC注册中心配置类，包含RPC注册中心相关的配置信息
 * @author dangzitou
 * @date 2026/02/18
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类别
     */
    private String registry = "etcd";

    /**
     * 注册中心地址，默认为"localhost:2379"
     */
    private String address = "http://localhost:2379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（毫秒）
     */
    private Long timeout = 10000L;
}
