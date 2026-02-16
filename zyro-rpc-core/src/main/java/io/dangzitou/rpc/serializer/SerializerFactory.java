package io.dangzitou.rpc.serializer;

import io.dangzitou.rpc.serializer.impl.HessianSerializer;
import io.dangzitou.rpc.serializer.impl.JdkSerializer;
import io.dangzitou.rpc.serializer.impl.JsonSerializer;
import io.dangzitou.rpc.serializer.impl.KryoSerializer;
import io.dangzitou.rpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取序列化器实例
     * @param serializerName 序列化器的键
     * @return 返回对应的序列化器实例，如果没有找到则返回默认序列化器
     */
    public  static Serializer getInstance(String serializerName) {
        return SpiLoader.getInstance(Serializer.class, serializerName);
    }
}
