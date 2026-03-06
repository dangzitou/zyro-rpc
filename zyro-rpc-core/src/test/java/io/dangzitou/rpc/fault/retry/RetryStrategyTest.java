package io.dangzitou.rpc.fault.retry;

import io.dangzitou.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class RetryStrategyTest {
    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry(){
        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }

    }
}
