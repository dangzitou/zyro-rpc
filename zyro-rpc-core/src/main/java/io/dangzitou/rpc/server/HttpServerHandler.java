package io.dangzitou.rpc.server;

import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.model.RpcRequest;
import io.dangzitou.rpc.model.RpcResponse;
import io.dangzitou.rpc.registry.LocalRegistry;
import io.dangzitou.rpc.serializer.Serializer;
import io.dangzitou.rpc.serializer.SerializerFactory;
import io.dangzitou.rpc.serializer.impl.JdkSerializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.lang.reflect.Method;

@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 处理HTTP请求
     * @param request HTTP请求对象
     */
    @Override
    public void handle(HttpServerRequest request) {
        //指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        //记录日志
        log.info("Received request: {} {}", request.method(), request.uri());
        //异步处理HTTP请求
        request.bodyHandler(body -> {
            //反序列化
            byte[] requestData = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(requestData, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //处理RPC请求
            RpcResponse rpcResponse = new RpcResponse();
            //如果请求为null，直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                rpcResponse.setException(new IllegalArgumentException("Request cannot be null"));
                doResponse(request, rpcResponse, serializer);
                return;
            }
            try{
                //获取要调用的服务实现类，并通过反射调用方法
                Class<?> serviceClass = LocalRegistry.getService(rpcRequest.getServiceName());
                if (serviceClass == null) {
                    rpcResponse.setMessage("Service not found: " + rpcRequest.getServiceName());
                    rpcResponse.setException(new ClassNotFoundException("Service not found"));
                    doResponse(request, rpcResponse, serializer);
                    return;
                }
                Method method = serviceClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());

                // 转换参数类型（解决 JSON 序列化导致的 LinkedHashMap 问题）
                Object[] args = rpcRequest.getParams();
                Class<?>[] paramTypes = rpcRequest.getParamTypes();
                if (args != null && paramTypes != null) {
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] != null && !paramTypes[i].isInstance(args[i])) {
                            args[i] = objectMapper.convertValue(args[i], paramTypes[i]);
                        }
                    }
                }
                Object result = method.invoke(serviceClass.newInstance(), args);
                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("Success");
            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage("Error processing request: " + e.getMessage());
                rpcResponse.setException(e);
            }
            //响应结果
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 响应RPC结果
     * @param request HTTP请求对象
     * @param rpcResponse RPC响应对象
     * @param serializer 序列化器
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request.response().putHeader("content-type", "application/json");
        try {
            byte[] responseData = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(responseData));
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatusCode(500).end(Buffer.buffer());
        }
    }
}
