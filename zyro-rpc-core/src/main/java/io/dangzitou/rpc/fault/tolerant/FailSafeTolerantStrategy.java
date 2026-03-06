package io.dangzitou.rpc.fault.tolerant;

import io.dangzitou.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 记录异常日志，但不抛出异常，返回一个默认的RpcResponse
        log.info("FailSafeTolerantStrategy: {}", e.getMessage());
        return new RpcResponse(); // 返回一个默认的RpcResponse对象，可以根据需要进行定制
    }
}
