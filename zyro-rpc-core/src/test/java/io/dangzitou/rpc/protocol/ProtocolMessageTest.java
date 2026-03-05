package io.dangzitou.rpc.protocol;

import cn.hutool.core.util.IdUtil;
import io.dangzitou.rpc.constant.RpcConstant;
import io.dangzitou.rpc.model.RpcRequest;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.Args;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class ProtocolMessageTest {
    @Test
    public void testEncodeAndDecode() throws Exception {
        //构造消息
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("myService");
        rpcRequest.setMethodName("myMethod");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParamTypes(new Class[]{String.class});
        rpcRequest.setParams(new Object[]{"hello", "world"});

        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);

        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
        ProtocolMessage<?> message = ProtocolMessageDecoder.decode(encodeBuffer);
        Assert.assertNotNull(message);

        //log.info("Encoded buffer: {}", encodeBuffer);
        log.info("Decoded message: {}", message);
    }
}
