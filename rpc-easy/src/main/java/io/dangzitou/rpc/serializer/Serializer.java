package io.dangzitou.rpc.serializer;

import java.io.IOException;

public interface Serializer {
    /**
     * 序列化
     * @param object 待序列化的对象
     * @param <T> 待序列化的对象类型
     * @return 序列化后的字节数组
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     * @param bytes 待反序列化的字节数组
     * @param type 反序列化后的对象类型
     * @param <T> 反序列化后的对象类型
     * @return 反序列化后的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
