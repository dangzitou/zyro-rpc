package io.dangzitou.rpc.fault.retry;

import io.dangzitou.rpc.spi.SpiLoader;

public class RetryStrategyFactory {
    static{
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试策略
     */
    private static final RetryStrategy DEFAULT_STRATEGY = new NoRetryStrategy();

    /**
     * 获取默认的重试策略实例
     * @param strategyKey
     * @return
     */
    public static RetryStrategy getInstance(String strategyKey) {
        return SpiLoader.getInstance(RetryStrategy.class, strategyKey);
    }
}
