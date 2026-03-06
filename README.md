# e-rpc

一个基于 Java 21 的 RPC 学习项目，目标是从零实现并演进一套可运行的 RPC 框架。

项目当前包含两套实现思路：
- `rpc-easy`：最小可用版（便于理解 RPC 基础调用链路）
- `zyro-rpc-core`：进阶版（包含可扩展序列化、负载均衡、重试、注册中心、TCP 协议通信等能力）

## 1. 项目模块

- `example-common`：公共模型与服务接口（Provider/Consumer 共享）
- `example-provider`：服务提供者示例
- `example-consumer`：服务消费者示例
- `rpc-easy`：轻量 RPC 原型实现
- `zyro-rpc-core`：进阶 RPC 核心实现

## 2. 技术栈

- JDK 21
- Maven 3.9+
- Vert.x（网络通信）
- SPI（可扩展组件加载）
- Etcd / ZooKeeper（注册中心，core 版）

## 3. 功能概览

`rpc-easy`（最小版）：
- 请求/响应模型
- 本地服务注册（LocalRegistry）
- HTTP 通信
- 动态代理调用
- JDK 序列化

`zyro-rpc-core`（进阶版）：
- 自定义协议编解码
- TCP 通信（Vert.x TCP）
- 多序列化实现（JDK / JSON / Kryo / Hessian）
- 注册中心扩展（Etcd / ZooKeeper）
- 负载均衡策略（随机 / 轮询 / 一致性哈希）
- 重试策略（无重试 / 固定间隔重试）
- SPI 扩展加载机制

## 4. 快速开始

### 4.1 环境准备

- 安装 JDK 21
- 安装 Maven 3.9+

如果要运行 `zyro-rpc-core` 示例，默认还需要：
- 启动 Etcd（默认地址 `http://localhost:2379`）

### 4.2 编译项目

在项目根目录执行：

```bash
mvn -DskipTests package
```

## 5. 运行示例

### 5.1 最小版链路（不依赖外部注册中心）

1. 启动 Provider：
- 主类：`io.dangzitou.example.provider.EasyProviderExample`
- 默认端口：`8081`

2. 启动 Consumer：
- 主类：`io.dangzitou.example.consumer.EasyConsumerExample`

这条链路主要用于快速验证：
- Provider 暴露服务
- Consumer 通过代理发起远程调用

### 5.2 进阶版链路（默认使用 Etcd + TCP）

1. 启动 Provider：
- 主类：`io.dangzitou.example.provider.ProviderExample`
- 默认端口：`8081`

2. 启动 Consumer：
- 主类：`io.dangzitou.example.consumer.ConsumerExample`

默认配置位于：
- `example-provider/src/main/resources/application.properties`
- `example-consumer/src/main/resources/application.properties`

## 6. 目录结构

```text
.
├── example-common
├── example-consumer
├── example-provider
├── rpc-easy
└── zyro-rpc-core
```

## 7. 后续优化方向

- 增加端到端压测模块（QPS / P95 / P99 / 错误率）
- 增加熔断与限流策略
- 完善集成测试与故障注入测试
- 补充调用链可观测性（日志追踪、指标上报）
