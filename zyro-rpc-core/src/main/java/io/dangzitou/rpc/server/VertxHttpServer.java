package io.dangzitou.rpc.server;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        //创建Vert.x实例
        Vertx vertx = Vertx.vertx();
        //创建HTTP服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        //监听端口并处理请求
        server.requestHandler(new HttpServerHandler())
                .listen(port, result -> {
            //处理服务器启动结果
            if (result.succeeded()) {
                log.info("Vert.x HTTP Server is now listening on port {}", port);
            } else {
                log.error("Failed to start Vert.x HTTP Server", result.cause());
            }
        });
    }
}
