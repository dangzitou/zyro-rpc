# e-rpc

A minimal RPC demo built on Java 21 and Vert.x, with a provider and consumer example. The project is a multi-module Maven build that shares a common service model.

## Modules

- `rpc-easy`: lightweight RPC core (request/response, registry, serializer, HTTP server)
- `example-common`: shared model and service interfaces
- `example-provider`: provider implementation and server bootstrap
- `example-consumer`: consumer that calls the remote service via proxy

## Prerequisites

- JDK 21
- Maven 3.9+

## Build

From the project root:

```bash
mvn -DskipTests package
```

## Run the examples

1) Start the provider

Run the main class `io.dangzitou.example.provider.EasyProviderExample` from your IDE.
It starts an HTTP server on port `8081`.

2) Run the consumer

Run the main class `io.dangzitou.example.consumer.EasyConsumerExample` from your IDE.
It creates a proxy and invokes `UserService#getUser`.

## Notes

- The provider registers `UserService` in `LocalRegistry` and exposes it over HTTP.
- The consumer uses `ServiceProxyFactory` to build a client-side proxy.

## Project layout

```
.
├─ example-common/
├─ example-consumer/
├─ example-provider/
└─ rpc-easy/
```

