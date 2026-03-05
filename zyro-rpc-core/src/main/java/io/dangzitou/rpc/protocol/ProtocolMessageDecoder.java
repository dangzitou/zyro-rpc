package io.dangzitou.rpc.protocol;

import io.dangzitou.rpc.model.RpcRequest;
import io.dangzitou.rpc.model.RpcResponse;
import io.dangzitou.rpc.serializer.Serializer;
import io.dangzitou.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 协议消息解码器
 * 将接收到的字节数据解码为协议消息对象，以便后续处理
 */
@Slf4j
public class ProtocolMessageDecoder {
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        //校验魔数
        if(magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("Invalid magic number: " + magic);
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        int bodyLength = buffer.getInt(13);
        //log.info("bodyLength: {}", buffer.getInt(13));
        //解决粘包问题，只读取完整的消息体数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        //解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum
                .getEnumByKey(header.getSerializer());
        if(serializerEnum == null) {
            throw new RuntimeException("Unsupported serializer: " + header.getSerializer());
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum protocolMessageTypeEnum = ProtocolMessageTypeEnum
                .getEnumByKey(header.getType());
        if(protocolMessageTypeEnum == null) {
            throw new RuntimeException("Unsupported message type: " + header.getType());
        }
        return switch (protocolMessageTypeEnum) {
            case REQUEST -> new ProtocolMessage<>(header, serializer.deserialize(bodyBytes, RpcRequest.class));
            case RESPONSE -> new ProtocolMessage<>(header, serializer.deserialize(bodyBytes, RpcResponse.class));
            default -> throw new RuntimeException("Unsupported message type: " + header.getType());
        };
    }
}
