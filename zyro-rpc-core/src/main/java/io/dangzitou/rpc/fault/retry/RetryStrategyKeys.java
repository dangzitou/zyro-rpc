package io.dangzitou.rpc.fault.retry;

/**
 * 重试策略的常量定义接口，包含不同重试策略的标识符
 */
public interface RetryStrategyKeys {
    /**
     * 不重试
     */
    String NO_RETRY = "noRetry";

    /**
     * 固定间隔重试
     */
    String FIXED_INTERVAL = "fixedInterval";
}
