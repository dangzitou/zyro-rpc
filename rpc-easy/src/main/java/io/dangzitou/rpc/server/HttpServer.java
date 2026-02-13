package io.dangzitou.rpc.server;

/**
 * HTTP服务器接口
 * @author dangzitou
 * @date 2025/2/11
 */
public interface HttpServer {
    /**
     * 启动HTTP服务器
     * @param port 监听的端口号
     */
    void doStart(int port);
}
