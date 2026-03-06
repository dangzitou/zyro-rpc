package io.dangzitou.rpc.fault.retry;

import com.github.rholder.retry.*;
import io.dangzitou.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定间隔重试策略实现，使用Retryer进行重试，设置固定的等待时间和最大重试次数
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {
    /**
     * 执行重试逻辑，调用提供的Callable进行重试，设置固定的等待时间和最大重试次数
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(1000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("Retry attempt: {}/3, waiting 1 second before next attempt", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
