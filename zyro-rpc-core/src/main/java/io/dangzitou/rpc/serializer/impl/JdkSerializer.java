package io.dangzitou.rpc.serializer.impl;

import io.dangzitou.rpc.serializer.Serializer;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * 基于 Java 自带的序列化器实现 JdkSerializer
 */
public class JdkSerializer implements Serializer {
    /**
     * 序列化
     * @param object 待序列化的对象
     * @param <T> 待序列化的对象类型
     * @return 序列化后的字节数组
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        if (object == null) {
            return new byte[0];
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            oos.flush();
            return baos.toByteArray();
        }
    }

    /**
     * 反序列化
     * @param bytes 待反序列化的字节数组
     * @param type 反序列化后的对象类型
     * @param <T> 反序列化后的对象类型
     * @return 反序列化后的对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            Object obj = ois.readObject();
            return type.cast(obj);
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
