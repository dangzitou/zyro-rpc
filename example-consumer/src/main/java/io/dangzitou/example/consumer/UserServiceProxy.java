package io.dangzitou.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import io.dangzitou.example.common.model.User;
import io.dangzitou.example.common.service.UserService;
import io.dangzitou.rpc.model.RpcRequest;
import io.dangzitou.rpc.model.RpcResponse;
import io.dangzitou.rpc.serializer.Serializer;
import io.dangzitou.rpc.serializer.impl.JdkSerializer;

/**
 * UserService的代理类，通过HTTP请求调用远程服务
 * @author dangzitou
 * @date 2025/2/11
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        //指定序列化器
        Serializer serializer = new JdkSerializer();
        //发请求
        RpcRequest request = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .paramTypes(new Class<?>[]{User.class})
                .params(new Object[]{user})
                .build();
        try {
            byte[] requestData = serializer.serialize(request);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8081")
                    .body(requestData)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            //反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
