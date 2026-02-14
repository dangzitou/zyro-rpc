package io.dangzitou.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类，提供一些常用的配置相关的工具方法
 * @author dangzitou
 * @date 2025/02/13
 */
public class ConfigUtils {
    /**
     * 根据前缀加载配置
     * @param tClass
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 根据前缀和环境加载配置
     * @param tClass
     * @param prefix
     * @param environment
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if(StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        //props是Hutool提供的一个配置类，可以从指定的配置文件中加载配置项，并提供一些便捷的方法来获取配置值
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(tClass, prefix);
    }
}
