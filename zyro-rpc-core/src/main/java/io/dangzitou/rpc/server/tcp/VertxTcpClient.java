package io.dangzitou.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.model.RpcRequest;
import io.dangzitou.rpc.model.RpcResponse;
import io.dangzitou.rpc.model.ServiceMetaInfo;
import io.dangzitou.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class VertxTcpClient {
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException, TimeoutException {
        //发送tcp请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();//异步连接服务器
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                response -> {
                    if (response.succeeded()) {
                        log.info("Connected to server: {}:{}", serviceMetaInfo.getServiceHost(), serviceMetaInfo.getServicePort());
                        NetSocket socket = response.result();
                        //发送请求数据
                        //构造消息
                        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                        ProtocolMessage.Header header = new ProtocolMessage.Header();
                        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                        header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                        header.setRequestId(IdUtil.getSnowflakeNextId());
                        protocolMessage.setHeader(header);
                        protocolMessage.setBody(rpcRequest);
                        //编码请求
                        try {
                            Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                            socket.write(encodeBuffer);
                        } catch (IOException e) {
                            responseFuture.completeExceptionally(e);//编码失败，完成异常
                            throw new RuntimeException("Failed to encode request: " + e.getMessage());
                        }

                        //接收响应
                        socket.handler(buffer -> {
                            try {
                                ProtocolMessage<RpcResponse> responseMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                responseFuture.complete(responseMessage.getBody());//解码成功，完成响应
                            } catch (Exception e) {
                                responseFuture.completeExceptionally(e);//解码失败，完成异常
                                throw new RuntimeException("Failed to decode response: " + e.getMessage());
                            }
                        });
                    } else {
                        log.error("Failed to connect to server: {}:{} , error: {} ", serviceMetaInfo.getServiceAddress(), serviceMetaInfo.getServicePort(), response.cause().getMessage());
                        responseFuture.completeExceptionally(response.cause());
                    }
                });
        RpcResponse rpcResponse = responseFuture.get(5, TimeUnit.SECONDS);
        //关闭连接
        netClient.close();
        return rpcResponse;
    }
}
