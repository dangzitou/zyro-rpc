package io.dangzitou.rpc.protocol;

import io.dangzitou.rpc.serializer.Serializer;
import io.dangzitou.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 协议消息编码器
 * 将协议消息对象编码为字节数据，以便在网络上传输
 */
@Slf4j
public class ProtocolMessageEncoder {
    /**
     * 将协议消息对象编码为字节数据
     * @param protocolMessage
     * @return
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if(protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }

        ProtocolMessage.Header header = protocolMessage.getHeader();
        //依次向Buffer中写入协议消息的各个字段
        //结构：魔数(1字节) + 版本号(1字节) + 序列化算法(1字节) + 消息类型(1字节) + 状态(1字节) + 请求ID(8字节)
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        //获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum
                .getEnumByKey(header.getSerializer());
        if(serializerEnum == null) {
            throw new RuntimeException("Unsupported serializer: " + header.getSerializer());
        }

        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        //写入消息体长度和数据
        //log.info("bodyLength: {}", bodyBytes.length);
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
