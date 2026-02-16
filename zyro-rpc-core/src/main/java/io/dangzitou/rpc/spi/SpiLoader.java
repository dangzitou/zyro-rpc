package io.dangzitou.rpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import io.dangzitou.rpc.serializer.Serializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SPI加载器，负责加载和管理SPI实现类的实例
 * @author dangzitou
 * @date 2026/02/16
 */
@Slf4j
@Data
public class SpiLoader {
    /**
     * SPI加载器映射，存储不同SPI接口的实现类
     * 接口名=>{键=>实现类}，例如：io.dangzitou.rpc.serializer.Serializer={json=JsonSerializer.class, jdk=JdkSerializer.class}
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * SPI实例缓存，存储已经创建的SPI实例，避免重复创建
     * 实现类名=>实例，例如：io.dangzitou.rpc.serializer.impl.JsonSerializer=JsonSerializer实例
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_PATHS = {RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = List.of(Serializer.class);

    /**
     * 加载所有SPI实现类，扫描指定目录下的SPI配置文件，并将实现类加载到内存中
     */
    public static void loadAll() {
        log.info("加载所有SPI实现类...");
        for (Class<?> clazz : LOAD_CLASS_LIST){
            load(clazz);
        }
    }

    /**
     * 获取某个接口的实例
     * @param spiInterface SPI接口类
     */
    public static <T> T getInstance(Class<T> spiInterface, String key) {
        String className = spiInterface.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(className);
        if (keyClassMap == null) {
            throw new RuntimeException("SPI接口 " + className + " 没有找到任何实现类");
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException("SPI接口 " + className + " 没有找到键为 " + key + " 的实现类");
        }
        //获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        //从实例缓存中加载指定类型的实例
        if(!instanceCache.containsKey(implClassName)){
            try {
                //创建实现类的实例，并将其缓存起来
                Object instance = implClass.getDeclaredConstructor().newInstance();
                instanceCache.put(implClassName, instance);
            } catch (Exception e) {
                throw new RuntimeException("无法创建SPI接口 " + className + " 的实现类 " + implClassName + " 的实例", e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    /**
     * 加载某个类型的SPI实现类，扫描指定目录下的SPI配置文件，并将实现类加载到内存中
     * @param loadClass SPI接口类
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载SPI接口 {} 的实现类...", loadClass.getName());
        //扫描路径
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanPath : SCAN_PATHS) {
            List<URL> resources = ResourceUtil.getResources(scanPath + loadClass.getName());
            //读取每个资源文件
            for(URL resource : resources){
                InputStreamReader reader = new InputStreamReader(ResourceUtil.getStream(String.valueOf(resource)));
                BufferedReader bufferedReader = new BufferedReader(reader);
                bufferedReader.lines().forEach(line -> {
                    //解析每行内容，格式为：键=实现类全名
                    String[] parts = line.split("=");
                    if (parts.length != 2) {
                        log.warn("SPI配置文件 {} 中的行 {} 格式不正确，应该为 键=实现类全名", resource.getPath(), line);
                        return;
                    }
                    String key = parts[0].trim();
                    String className = parts[1].trim();
                    try {
                        //加载实现类，并将其与键关联起来
                        Class<?> implClass = Class.forName(className);
                        keyClassMap.put(key, implClass);
                        log.info("成功加载SPI接口 {} 的实现类 {}，键为 {}", loadClass.getName(), className, key);
                    } catch (ClassNotFoundException e) {
                        log.error("无法加载SPI接口 {} 的实现类 {}，请确保类路径正确", loadClass.getName(), className, e);
                    }
                });
            }
        }
        if (keyClassMap.isEmpty()) {
            log.warn("SPI接口 {} 没有找到任何实现类", loadClass.getName());
        }
        //将接口与其实现类映射关系存储到加载器映射中
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    public static void main(String[] args) {
        loadAll();
        log.info("SPI加载器映射：{}", loaderMap);
        Serializer serializer = getInstance(Serializer.class, "json");
        log.info("获取到的序列化器实例：{}", serializer);
    }
}