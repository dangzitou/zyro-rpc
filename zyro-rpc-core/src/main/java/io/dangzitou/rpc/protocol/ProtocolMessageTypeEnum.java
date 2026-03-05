package io.dangzitou.rpc.protocol;

import lombok.Getter;

/**
 * 协议消息类型枚举，区分请求、响应、心跳等不同类型的消息
 */
@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);

    private final int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    /**
     * 获取消息类型枚举
     * @param key
     * @return
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum type : ProtocolMessageTypeEnum.values()) {
            if (type.getKey() == key) {
                return type;
            }
        }
        return null;
    }
}
