# SDK Example – What it does and what you’ll see

This describes running `sdk-example` and the expected console output.

## How to run
```bash
cd sdk-example
mvn spring-boot:run
```
(Prereq: the parent build `mvn clean install` has been run so dependencies are present.)

## Try the demo API
- Success path:
  - `curl "http://localhost:8080/api/demo?outcome=success"`
  - Expected response: `demo ok`
  - Console: log + metric + trace (start and completion), enriched with service/env/path/method/outcome/duration
- Failure path:
  - `curl "http://localhost:8080/api/demo?outcome=fail"`
  - Expected response: HTTP 500 with body `simulated failure`
  - Console still shows log/metric/trace; completion events carry status=error and duration.

## What happens
- On startup, the sample emits one log, one metric, and one trace via `ObservabilityClient`.
- Splunk/Argus adapters are **disabled by default** in `sdk-example/src/main/resources/application.yml`, so no HTTP calls are made.
- The custom adapter is enabled and wired to console `Consumer` handlers, so you see the events printed locally.

## Expected console output (representative)
```
... Started ObservabilityExampleApplication ...
Custom log sink => SDK bootstrap complete
Custom metric sink => app.startup: 1.000000
```
(Trace emission has no custom handler in the sample, so it’s silent by default.)

## Enabling Splunk or Argus
- Set `observability.splunk.enabled=true` (and endpoint/token) to push the same events to Splunk HEC.
- Set `observability.argus.enabled=true` (and endpoint/token) to push the same events to Argus.
- With adapters enabled, the console messages still appear (custom handler), and adapters send HTTP payloads as documented in `README.md` and `docs/USER_GUIDE.md`.

