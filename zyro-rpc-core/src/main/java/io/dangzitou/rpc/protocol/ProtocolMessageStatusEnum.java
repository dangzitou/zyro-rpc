package io.dangzitou.rpc.protocol;

import lombok.Getter;

/**
 * 协议消息状态枚举，表示请求处理的结果，如成功、失败、异常等
 */
@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok", 200),
    BAD_REQUEST("bad request", 40),
    BAD_RESPONSE("bad response", 50);

    private final String text;//状态文本描述

    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据状态码获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum status : ProtocolMessageStatusEnum.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
