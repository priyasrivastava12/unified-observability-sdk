# High-Level Design (HLD)

This SDK follows a ports-and-adapters (hexagonal) style. The core defines a port (`ObservabilityAdapter`) and domain models; adapters plug into it; applications depend only on the core API (`ObservabilityClient`).

## Block view
- **App layer (your service or `sdk-example`)**
  - Calls `ObservabilityClient.emitLog/emitMetric/emitTrace`.
  - May define `ObservabilityEnricher` beans to add service/env/etc.
  - May define custom `Consumer` sinks (via `adapter-custom`).
- **Core (`sdk-core`)**
  - Models: `StructuredLog`, `MetricEvent`, `TraceSpan`, enums (`LogLevel`, `MetricType`).
  - Port: `ObservabilityAdapter` interface.
  - Orchestrator: `ObservabilityClient` (runs enrichers, fans out to adapters).
  - Enrichment hook: `ObservabilityEnricher`.
  - Auto-config: `ObservabilityAutoConfiguration` (`observability.enabled=true`).
- **Adapters**
  - `adapter-splunk`: HTTP HEC; bearer token; gated by `observability.splunk.enabled`.
  - `adapter-argus`: HTTP ingest; bearer token; gated by `observability.argus.enabled`.
  - `adapter-custom`: in-process `Consumer` sinks; gated by `observability.custom.enabled`.
- **Config & wiring**
  - Auto-config discovery via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` in each module.
  - `application.yml` toggles adapters/endpoints; `all-adapters` profile in `sdk-example` turns on Splunk+Argus.

## Data flow
1) App calls `emitLog/emitMetric/emitTrace`.
2) `ObservabilityClient` applies all `ObservabilityEnricher` beans (adds service/env/etc.).
3) Enriched event is broadcast to all active `ObservabilityAdapter` beans.
4) Each adapter delivers to its backend (Splunk HEC, Argus HTTP) or invokes custom Consumers; optional `flush/shutdown`.

## Why hexagonal (ports-and-adapters)
- Domain/port in `sdk-core`; adapters are replaceable modules.
- App depends only on the port/client; enabling/disabling is config-driven, not code changes.
- Adapters can be added without touching the core or application code.

## How to extend
- Add a new backend: implement `ObservabilityAdapter`, publish as a module with auto-config and an imports file.
- Add common context: provide an `ObservabilityEnricher` bean.
- Add custom handling: provide `Consumer<StructuredLog|MetricEvent|TraceSpan>` beans (with `adapter-custom`).

