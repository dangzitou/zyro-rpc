package io.dangzitou.rpc.fault.tolerant;

import io.dangzitou.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 降级到其他服务的容错策略实现，在发生异常时返回一个预定义的降级响应，或者调用一个备用服务进行处理
 */
public class FailBackTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 这里可以根据上下文信息和异常信息，返回一个预定义的降级响应
        // 也可以调用一个备用服务进行处理，或者返回一个默认的RpcResponse对象
        // 下面是一个简单的示例，返回一个默认的RpcResponse对象
        RpcResponse fallbackResponse = new RpcResponse();
        fallbackResponse.setMessage("FailBackTolerantStrategy: " + e.getMessage());
        return fallbackResponse;
    }
}
