package io.dangzitou.rpc.server.tcp;

import io.dangzitou.rpc.model.RpcRequest;
import io.dangzitou.rpc.model.RpcResponse;
import io.dangzitou.rpc.protocol.ProtocolMessage;
import io.dangzitou.rpc.protocol.ProtocolMessageDecoder;
import io.dangzitou.rpc.protocol.ProtocolMessageEncoder;
import io.dangzitou.rpc.protocol.ProtocolMessageTypeEnum;
import io.dangzitou.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        //处理连接
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            //接收请求，解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try{
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            }catch (Exception e){
                throw new RuntimeException("Failed to decode protocol message: " + e.getMessage(), e);
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            //处理请求，执行方法调用等逻辑
            RpcResponse rpcResponse = new RpcResponse();
            try{
                //获取需要调用的服务和方法信息，通过反射调用方法，获取结果
                Class<?> imlClass = LocalRegistry.getService(rpcRequest.getServiceName());
                Method method = imlClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                Object result = method.invoke(imlClass.getDeclaredConstructor().newInstance(), rpcRequest.getParams());
                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseMessage = new ProtocolMessage<>(header, rpcResponse);
            try{
                Buffer encode = ProtocolMessageEncoder.encode(responseMessage);
                netSocket.write(encode);
            }catch (Exception e){
                throw new RuntimeException("Failed to encode protocol message: " + e.getMessage(), e);
            }
        });
        netSocket.handler(tcpBufferHandlerWrapper);
    }
}
