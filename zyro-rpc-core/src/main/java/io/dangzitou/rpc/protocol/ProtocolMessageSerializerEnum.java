package io.dangzitou.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageSerializerEnum {
    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian");

    private final int key;

    private final String value;

    private ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 根据key获取枚举
     * @param key
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum serializer : ProtocolMessageSerializerEnum.values()) {
            if (serializer.getKey() == key) {
                return serializer;
            }
        }
        return null;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        for (ProtocolMessageSerializerEnum serializer : ProtocolMessageSerializerEnum.values()) {
            if (serializer.getValue().equalsIgnoreCase(value)) {
                return serializer;
            }
        }
        return null;
    }
}
