# zyro-rpc

一个基于 Java 21 的 RPC 学习项目，目标是从零实现一个可运行、可扩展、可集成 Spring Boot 的 RPC 框架。

项目目前包含三条主线：

- `rpc-easy`：最小可用版，便于理解一次 RPC 调用的基础链路
- `zyro-rpc-core`：完整框架版，支持协议编解码、注册中心、负载均衡、重试与容错
- `zyro-rpc-springboot-starter`：Spring Boot Starter，支持注解式服务暴露与引用

## 项目结构

```text
.
├── example-common
├── example-consumer
├── example-provider
├── example-springboot-consumer
├── example-springboot-provider
├── rpc-easy
├── zyro-rpc-core
└── zyro-rpc-springboot-starter
```

各模块职责：

- `example-common`：公共模型与服务接口
- `example-provider`：Java 原生 Provider 示例
- `example-consumer`：Java 原生 Consumer 示例
- `example-springboot-provider`：Spring Boot Provider 示例
- `example-springboot-consumer`：Spring Boot Consumer 示例
- `rpc-easy`：HTTP + JDK 序列化的简化版 RPC 实现
- `zyro-rpc-core`：核心框架实现
- `zyro-rpc-springboot-starter`：Starter 与注解集成层

## 技术栈

- JDK 21
- Maven 3.9+
- Vert.x
- SPI 扩展加载
- Etcd / ZooKeeper 注册中心
- Spring Boot Starter（注解式接入）

## 核心能力

`rpc-easy` 提供：

- 请求 / 响应模型
- 本地服务注册
- HTTP 通信
- 动态代理调用
- JDK 序列化

`zyro-rpc-core` 提供：

- 自定义 RPC 协议与编解码
- 基于 Vert.x 的 TCP 通信
- 多序列化实现：`jdk`、`json`、`kryo`、`hessian`
- 注册中心扩展：`etcd`、`zookeeper`
- 负载均衡：`roundRobin`、`random`、`consistentHash`
- 重试策略：`noRetry`、`fixedInterval`
- 容错策略：`failFast`、`failOver`、`failSafe`、`failBack`
- SPI 扩展点加载机制

`zyro-rpc-springboot-starter` 提供：

- `@EnableRpc`
- `@RpcService`
- `@RpcReference`

## 配置说明

`zyro-rpc-core` 默认配置以 `RpcConfig` / `RegistryConfig` 为准：

- `rpc.name`：默认 `zyro-rpc`
- `rpc.version`：默认 `1.0.0`
- `rpc.serverHost`：默认 `localhost`
- `rpc.serverPort`：默认 `8081`
- `rpc.serializer`：默认 `jdk`
- `rpc.loadBalancer`：默认 `roundRobin`
- `rpc.retryStrategy`：默认 `noRetry`
- `rpc.tolerantStrategy`：默认 `failFast`
- `rpc.registryConfig.registry`：默认 `etcd`
- `rpc.registryConfig.address`：默认 `http://localhost:2379`

示例项目中的配置文件位于：

- `example-provider/src/main/resources/application.properties`
- `example-consumer/src/main/resources/application.properties`
- `example-springboot-provider/src/main/resources/application.properties`
- `example-springboot-consumer/src/main/resources/application.properties`

## 快速开始

### 1. 环境准备

- 安装 JDK 21
- 安装 Maven 3.9+
- 如果运行 `zyro-rpc-core` 或 Spring Boot 示例，准备 Etcd，默认地址为 `http://localhost:2379`

### 2. 编译

在项目根目录执行：

```bash
mvn -DskipTests package
```

## 运行示例

### 1. 最小版链路

不依赖注册中心，适合先跑通基础调用流程。

启动 Provider：

- 主类：`io.dangzitou.example.provider.EasyProviderExample`

启动 Consumer：

- 主类：`io.dangzitou.example.consumer.EasyConsumerExample`

### 2. Core 链路

默认使用 TCP + Etcd。

启动 Provider：

- 主类：`io.dangzitou.example.provider.ProviderExample`

启动 Consumer：

- 主类：`io.dangzitou.example.consumer.ConsumerExample`

### 3. Spring Boot 链路

基于 Starter，通过注解完成服务发布与引用。

启动 Provider：

- 主类：`io.dangzitou.examplespringbootprovider.ExampleSpringbootProviderApplication`

启动 Consumer：

- 主类：`io.dangzitou.examplespringbootconsumer.ExampleSpringbootConsumerApplication`

对应注解：

- `@EnableRpc`：开启 RPC 自动装配，可通过 `needServer` 控制是否启动服务端
- `@RpcService`：标记服务提供者
- `@RpcReference`：注入远程代理，并可单独指定负载均衡、重试、容错与 mock

## 学习路径建议

如果你是按源码学习，建议顺序如下：

1. 先看 `rpc-easy`，理解请求模型、序列化、代理调用和服务端处理流程
2. 再看 `zyro-rpc-core`，重点关注协议编解码、注册发现、负载均衡和容错设计
3. 最后看 `zyro-rpc-springboot-starter`，理解框架如何接入 Spring 容器

## 测试

当前仓库已包含部分单元测试，主要覆盖：

- 协议编解码
- 注册中心
- 负载均衡
- 重试策略

可在根目录执行：

```bash
mvn test
```
