# unified-observability-sdk

Unified Spring Boot friendly SDK for structured logs, metrics, and traces with pluggable adapters for Splunk, Argus, and any custom pipeline.

## Modules
- `sdk-core` – domain models, `ObservabilityClient`, auto-config.
- `adapter-splunk` – Splunk HEC adapter (HTTP).
- `adapter-argus` – Argus HTTP adapter.
- `adapter-custom` – user-provided Consumer hooks.
- `sdk-example` – runnable Spring Boot sample.

## Quick start
1) Build everything:
```bash
mvn clean install
```
2) Run the example app:
```bash
cd sdk-example
mvn spring-boot:run
```

## Configuration
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

- Provide `Consumer<StructuredLog>`, `Consumer<MetricEvent>`, or `Consumer<TraceSpan>` beans to route events to any custom sink (see `sdk-example`).
- Each adapter is enabled via `observability.<adapter>.enabled=true`.

## Adapter payload examples
- Splunk HEC request body shape (example log):
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
    "attributes": {
      "component": "example"
    }
  }
}
```

## Docs
- [docs/USER_GUIDE.md](docs/USER_GUIDE.md) – how to add the SDK, configure, and emit events.
- [docs/SDK_EXAMPLE_RUN.md](docs/SDK_EXAMPLE_RUN.md) – how to run the sample app and what output to expect.
- [docs/HLD.md](docs/HLD.md) – high-level design and hexagonal (ports/adapters) view.
- Argus request body shape (example metric):
```json
{
  "type": "metric",
  "data": {
    "name": "app.startup",
    "value": 1.0,
    "unit": "count",
    "type": "COUNTER",
    "timestamp": "2023-11-15T12:00:00Z",
    "tags": {
      "env": "prod"
    }
  }
}
```
