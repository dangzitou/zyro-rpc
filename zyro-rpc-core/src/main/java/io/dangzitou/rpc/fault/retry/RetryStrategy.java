package io.dangzitou.rpc.fault.retry;

import io.dangzitou.rpc.model.RpcRequest;
import io.dangzitou.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略接口，定义了重试的行为
 */
public interface RetryStrategy {
    /**
     * 执行重试逻辑，调用提供的Callable进行重试
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
