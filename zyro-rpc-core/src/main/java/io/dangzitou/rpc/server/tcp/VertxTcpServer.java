package io.dangzitou.rpc.server.tcp;

import io.dangzitou.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServer implements HttpServer {
    private byte[] handleRequest(byte[] requestData) {
        return "Hello, client!".getBytes();
    }

    @Override
    public void doStart(int port) {
        //创建vert.x实例
        Vertx vertx = Vertx.vertx();

        //创建TCP服务器
        NetServer server = vertx.createNetServer();

        server.connectHandler(new TcpServerHandler());

        //启动服务器并监听指定端口
        server.listen(port, res -> {
            if (res.succeeded()) {
                log.info("TCP server started on port: {} ", port);
            } else {
                log.error("TCP server start failed: {} ", res.cause().getMessage());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
