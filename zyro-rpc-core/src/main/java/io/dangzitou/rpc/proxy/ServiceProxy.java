package io.dangzitou.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.model.RpcRequest;
import io.dangzitou.rpc.model.RpcResponse;
import io.dangzitou.rpc.serializer.Serializer;
import io.dangzitou.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理类(ServiceProxy)，通过动态代理实现对远程服务的调用
 * @author dangzitou
 * @date 2025/2/11
 */
public class ServiceProxy implements InvocationHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //构造请求
        RpcRequest request = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .params(args)
                .build();

        try {
            //序列化
            byte[] requestData = serializer.serialize(request);
            //发送请求并获取响应
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8081")
                    .body(requestData)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                //反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);

                // 将响应数据转换为正确的返回类型（解决 JSON 序列化导致的 LinkedHashMap 问题）
                Object data = rpcResponse.getData();
                if (data != null) {
                    Class<?> returnType = method.getReturnType();
                    if (!returnType.isInstance(data)) {
                        data = objectMapper.convertValue(data, returnType);
                    }
                }
                return data;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
