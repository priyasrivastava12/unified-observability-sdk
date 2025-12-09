# Unified Observability SDK – User Guide

This guide shows how to add the SDK to your Spring Boot app, configure adapters, and emit logs/metrics/traces.

## 1) Add dependencies
Add the modules you need to your app’s `pom.xml`:
```xml
<dependencies>
  <dependency>
    <groupId>com.example</groupId>
    <artifactId>sdk-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>com.example</groupId>
    <artifactId>adapter-splunk</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>com.example</groupId>
    <artifactId>adapter-argus</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>com.example</groupId>
    <artifactId>adapter-custom</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
</dependencies>
```
Include only the adapters you need.

## 2) Configure in `application.yml`
Enable adapters and set endpoints/tokens:
```yaml
observability:
  enabled: true
  splunk:
    enabled: true
    endpoint: https://splunk.example.com/services/collector
    token: ${SPLUNK_HEC_TOKEN}
    source: unified-sdk
  argus:
    enabled: false
    endpoint: https://argus.example.com/ingest
    token: ${ARGUS_TOKEN}
  custom:
    enabled: true
    name: console-pipeline
```

## 3) Emit events via `ObservabilityClient`
Inject and call the client; every enabled adapter receives the event.
```java
@Autowired ObservabilityClient obs;

obs.emitLog(new StructuredLog(
    LogLevel.INFO, "hello world", Instant.now(),
    "trace-1", "span-1", Map.of("k", "v")));

obs.emitMetric(new MetricEvent(
    "requests", 1.0, "count",
    MetricType.COUNTER, Instant.now(),
    Map.of("route", "/ping")));

obs.emitTrace(new TraceSpan(
    "span-1", "trace-1", null,
    "example-op", Instant.now(),
    Instant.now(), Map.of("status", "ok")));
```

## 4) Optional: custom sinks
With `adapter-custom` on the classpath, provide `Consumer` beans to route events anywhere (e.g., console, message bus):
```java
@Bean Consumer<StructuredLog> customLogHandler() {
    return log -> System.out.println("LOG => " + log.getMessage());
}
@Bean Consumer<MetricEvent> customMetricHandler() {
    return m -> System.out.println("METRIC => " + m.getName());
}
@Bean Consumer<TraceSpan> customTraceHandler() {
    return span -> System.out.println("SPAN => " + span.getSpanId());
}
```

## 5) Optional: global enrichment
Provide an `ObservabilityEnricher` bean to stamp common context on every event before any adapter sees it (e.g., service/env/host/version):
```java
@Bean
ObservabilityEnricher enricher(@Value("${spring.application.name}") String service,
                               @Value("${observability.environment:local}") String env) {
    return new ObservabilityEnricher() {
        public StructuredLog enrich(StructuredLog log) {
            return new StructuredLog(log.getLevel(), log.getMessage(), log.getTimestamp(),
                    log.getTraceId(), log.getSpanId(),
                    merge(log.getAttributes(), Map.of("service", service, "environment", env)));
        }
        public MetricEvent enrich(MetricEvent metric) {
            return new MetricEvent(metric.getName(), metric.getValue(), metric.getUnit(),
                    metric.getType(), metric.getTimestamp(),
                    merge(metric.getTags(), Map.of("service", service, "environment", env)));
        }
        public TraceSpan enrich(TraceSpan span) {
            return new TraceSpan(span.getSpanId(), span.getTraceId(), span.getParentSpanId(),
                    span.getName(), span.getStartTime(), span.getEndTime(),
                    merge(span.getAttributes(), Map.of("service", service, "environment", env)));
        }
        private <K,V> Map<K,V> merge(Map<K,V> orig, Map<K,V> extra) {
            Map<K,V> m = new HashMap<>(orig == null ? Map.of() : orig);
            m.putAll(extra);
            return m;
        }
    };
}
```

## 6) Run the sample app (optional)
`sdk-example` demonstrates the setup:
```bash
cd sdk-example
mvn spring-boot:run
```
It emits a log, metric, and span on startup and delivers them to the custom consumer (console).

## 7) Payload shapes (reference)
- Splunk HEC body (log):
```json
{
  "time": 1700000000,
  "host": "application",
  "source": "unified-sdk",
  "event": {
    "level": "INFO",
    "message": "SDK bootstrap complete",
    "timestamp": "2023-11-15T12:00:00Z",
    "traceId": "abc",
    "spanId": "def",
    "attributes": { "component": "example" }
  }
}
```
- Argus body (metric):
```json
{
  "type": "metric",
  "data": {
    "name": "app.startup",
    "value": 1.0,
    "unit": "count",
    "type": "COUNTER",
    "timestamp": "2023-11-15T12:00:00Z",
    "tags": { "env": "prod" }
  }
}
```

## 7) Toggle adapters
- Enable/disable via `observability.<adapter>.enabled`.
- If an adapter is enabled but missing required properties (endpoint/token), startup will fail fast.

