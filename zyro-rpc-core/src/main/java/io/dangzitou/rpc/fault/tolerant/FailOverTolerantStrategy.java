package io.dangzitou.rpc.fault.tolerant;

import io.dangzitou.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 故障转移的容错策略实现，在发生异常时尝试切换到另一个可用的服务实例进行处理
 */
public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 这里可以根据上下文信息和异常信息，尝试切换到另一个可用的服务实例进行处理
        // 下面是一个简单的示例，直接返回一个默认的RpcResponse对象
        RpcResponse failoverResponse = new RpcResponse();
        failoverResponse.setMessage("FailOverTolerantStrategy: " + e.getMessage());
        return failoverResponse;
    }
}
