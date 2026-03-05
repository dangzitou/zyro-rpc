package io.dangzitou.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dangzitou.rpc.RpcApplication;
import io.dangzitou.rpc.config.RpcConfig;
import io.dangzitou.rpc.model.RpcRequest;
import io.dangzitou.rpc.model.RpcResponse;
import io.dangzitou.rpc.model.ServiceMetaInfo;
import io.dangzitou.rpc.protocol.*;
import io.dangzitou.rpc.registry.Registry;
import io.dangzitou.rpc.registry.RegistryFactory;
import io.dangzitou.rpc.serializer.Serializer;
import io.dangzitou.rpc.serializer.SerializerFactory;
import io.dangzitou.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.dangzitou.rpc.constant.RpcConstant.DEFAULT_SERVICE_VERSION;

/**
 * 服务代理类(ServiceProxy)，通过动态代理实现对远程服务的调用
 * @author dangzitou
 * @date 2025/2/11
 */
@Slf4j
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
            //从注册中心获取服务地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(request.getServiceName());
            serviceMetaInfo.setServiceVersion(DEFAULT_SERVICE_VERSION);
            String serviceKey = serviceMetaInfo.getServiceKey();
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("Service not found: " + request.getServiceName());
            }
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
            log.info("Service found: {}, service address: {}", request.getServiceName(), selectedServiceMetaInfo.getServiceAddress());

            //发送tcp请求
            RpcResponse rpcResponse = VertxTcpClient.doRequest(request, selectedServiceMetaInfo);
            return rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送HTTP请求
     * @param address
     * @param requestData
     * @return
     * @throws IOException
     */
    public RpcResponse sendHttpRequest(String address, byte[] requestData) throws IOException {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        try(HttpResponse httpResponse = HttpRequest.post(address)
                .body(requestData)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            //反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse;
        } catch (Exception e) {
            log.error("Failed to send HTTP request: {} ", e.getMessage());
            throw new IOException("Failed to send HTTP request", e);
        }
    }
}
