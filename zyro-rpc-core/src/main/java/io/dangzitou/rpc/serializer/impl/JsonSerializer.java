package io.dangzitou.rpc.serializer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dangzitou.rpc.serializer.Serializer;

import java.io.IOException;

/**
 * JSON序列化器，使用JSON格式进行序列化和反序列化
 */
public class JsonSerializer implements Serializer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        return OBJECT_MAPPER.readValue(bytes, type);
    }
}
