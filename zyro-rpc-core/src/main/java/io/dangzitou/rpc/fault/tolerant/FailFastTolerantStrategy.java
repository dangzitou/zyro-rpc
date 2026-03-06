package io.dangzitou.rpc.fault.tolerant;

import io.dangzitou.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败的容错策略实现，在发生异常时直接抛出异常，不进行任何容错处理
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 直接抛出异常，不进行任何容错处理
        throw new RuntimeException("FailFastTolerantStrategy: " + e.getMessage(), e);
    }
}
