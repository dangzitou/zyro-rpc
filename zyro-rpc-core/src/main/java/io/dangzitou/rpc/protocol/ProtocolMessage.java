package io.dangzitou.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议消息，包含消息头和消息体
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体
     */
    private T body;

    /**
     * 协议消息头
     */
    @Data
    public static class Header {
        /**
         * 魔数，保证安全性
         */
        private byte magic;

        /**
         * 协议版本，便于协议升级和兼容性处理
         */
        private byte version;

        /**
         * 序列化器
         */
        private byte serializer;

        /**
         * 消息类型，区分请求、响应、心跳等不同类型的消息
         */
        private byte type;

        /**
         * 状态码，表示请求处理的结果，如成功、失败、异常等
         */
        private byte status;

        /**
         * 请求ID，唯一标识一次RPC调用，便于请求和响应的匹配
         */
        private long requestId;

        /**
         * 消息体长度，便于读取消息体数据
         */
        private int bodyLength;
    }
}
