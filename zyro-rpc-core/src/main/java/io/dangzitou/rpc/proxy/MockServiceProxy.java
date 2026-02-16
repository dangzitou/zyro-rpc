package io.dangzitou.rpc.proxy;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * MockServiceProxy是一个用于模拟RPC服务调用的代理类，
 * 实现了InvocationHandler接口。当RPC框架处于模拟模式时，
 * RPC客户端将使用MockServiceProxy来处理远程调用请求。
 * MockServiceProxy会根据被调用方法的返回类型，返回一个默认值，
 * 以便在测试和开发阶段模拟RPC服务的行为。
 * @author dangzitou
 * @date 2025/02/13
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    //使用Faker库生成模拟数据
    private static final Faker faker = new Faker();
    /**
     * 调用代理
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //根据返回值的类型，返回一个默认值
        Class<?> returnType = method.getReturnType();
        log.info("Mock invoke: {}", method.getName());
        return getDefaultObject(returnType);
    }

    /**
     * 根据返回值的类型，返回一个默认值
     * @param returnType
     * @return
     */
    private Object getDefaultObject(Class<?> returnType) {
        // 1. 处理基本类型 (Primitive)
        if (returnType.isPrimitive()) {
            if (returnType == boolean.class) {
                return faker.bool().bool();
            } else if (returnType == short.class) {
                return (short) faker.number().numberBetween(0, 100);
            } else if (returnType == int.class) {
                return faker.number().randomDigit();
            } else if (returnType == long.class) {
                return faker.number().randomNumber();
            } else if (returnType == double.class) {
                return faker.number().randomDouble(2, 0, 1000);
            } else if (returnType == float.class) {
                return (float) faker.number().randomDouble(2, 0, 1000);
            } else if (returnType == byte.class) {
                return (byte) faker.number().numberBetween(0, 127);
            } else if (returnType == char.class) {
                return (char) faker.number().numberBetween(97, 122); // 生成随机小写字母
            }
        }

        // 2. 处理常用对象类型 (这是 Faker 最强的地方)
        if (returnType == String.class) {
            // 随机生成一个人名，模拟真实数据
            return faker.name().fullName();
        }

        // 3. 其他复杂对象暂时返回 null
        return null;
    }
}
