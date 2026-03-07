package io.dangzitou.zyrorpc.springboot.starter.annotation;

import io.dangzitou.rpc.constant.RpcConstant;
import io.dangzitou.rpc.fault.retry.RetryStrategyKeys;
import io.dangzitou.rpc.fault.tolerant.TolerantStrategyKeys;
import io.dangzitou.rpc.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    /**
     * 服务接口类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 版本
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 负载均衡器
     */
    String loadBalance() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    String retryStrategy() default RetryStrategyKeys.NO_RETRY;

    /**
     * 容错策略
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 模拟调用
     */
    boolean mock() default false;
}
