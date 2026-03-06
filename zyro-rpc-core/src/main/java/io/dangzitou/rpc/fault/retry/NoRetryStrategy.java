package io.dangzitou.rpc.fault.retry;

import io.dangzitou.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不进行重试的策略实现，直接调用提供的Callable
 */
public class NoRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        // 不进行重试，直接调用
        return callable.call();
    }
}
