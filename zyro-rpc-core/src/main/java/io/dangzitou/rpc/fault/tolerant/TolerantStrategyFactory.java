package io.dangzitou.rpc.fault.tolerant;

import io.dangzitou.rpc.spi.SpiLoader;

public class TolerantStrategyFactory {
    static{
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    /**
     * 获取容错策略实例
     * @param strategyKey
     * @return
     */
    public static TolerantStrategy getInstance(String strategyKey) {
        return SpiLoader.getInstance(TolerantStrategy.class, strategyKey);
    }
}
