package io.dangzitou.rpc.fault.tolerant;

import io.dangzitou.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略接口，定义了在发生异常时如何进行容错处理
 */
public interface TolerantStrategy {
    /**
     * 执行容错逻辑，根据上下文和异常信息返回一个容错的RpcResponse
     * @param context
     * @param e
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
