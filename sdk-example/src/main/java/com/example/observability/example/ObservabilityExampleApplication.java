package com.example.observability.example;

import com.example.observability.core.LogLevel;
import com.example.observability.core.MetricEvent;
import com.example.observability.core.MetricType;
import com.example.observability.core.ObservabilityClient;
import com.example.observability.core.ObservabilityEnricher;
import com.example.observability.core.StructuredLog;
import com.example.observability.core.TraceSpan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@SpringBootApplication
public class ObservabilityExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityExampleApplication.class, args);
    }

    @Bean
    CommandLineRunner demoRunner(ObservabilityClient client) {
        return args -> {
            client.emitLog(new StructuredLog(
                    LogLevel.INFO,
                    "SDK bootstrap complete",
                    Instant.now(),
                    UUID.randomUUID().toString(),
                    null,
                    Map.of("component", "example")));

            client.emitMetric(new MetricEvent(
                    "app.startup",
                    1.0,
                    "count",
                    MetricType.COUNTER,
                    Instant.now(),
                    Map.of("env", "local")));

            client.emitTrace(new TraceSpan(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    null,
                    "example-operation",
                    Instant.now(),
                    Instant.now().plusSeconds(1),
                    Map.of("status", "ok")));
        };
    }

    @Bean
    Consumer<StructuredLog> customLogHandler() {
        return log -> System.out.printf("Custom log sink => %s%n", log.getMessage());
    }

    @Bean
    Consumer<MetricEvent> customMetricHandler() {
        return metric -> System.out.printf("Custom metric sink => %s: %f%n", metric.getName(), metric.getValue());
    }

    @Bean
    Consumer<TraceSpan> customTraceHandler() {
        return span -> System.out.printf("Custom trace sink => span=%s trace=%s name=%s%n",
                span.getSpanId(), span.getTraceId(), span.getName());
    }

    @Bean
    ObservabilityEnricher defaultEnricher(@Value("${spring.application.name:observability-example}") String serviceName,
                                          @Value("${observability.environment:local}") String environment) {
        return new ObservabilityEnricher() {
            @Override
            public StructuredLog enrich(StructuredLog log) {
                Map<String, Object> merged = merge(log.getAttributes(), Map.of(
                        "service", serviceName,
                        "environment", environment));
                return new StructuredLog(log.getLevel(), log.getMessage(), log.getTimestamp(),
                        log.getTraceId(), log.getSpanId(), merged);
            }

            @Override
            public MetricEvent enrich(MetricEvent metric) {
                Map<String, String> merged = merge(metric.getTags(), Map.of(
                        "service", serviceName,
                        "environment", environment));
                return new MetricEvent(metric.getName(), metric.getValue(), metric.getUnit(),
                        metric.getType(), metric.getTimestamp(), merged);
            }

            @Override
            public TraceSpan enrich(TraceSpan span) {
                Map<String, Object> merged = merge(span.getAttributes(), Map.of(
                        "service", serviceName,
                        "environment", environment));
                return new TraceSpan(span.getSpanId(), span.getTraceId(), span.getParentSpanId(),
                        span.getName(), span.getStartTime(), span.getEndTime(), merged);
            }

            private <K, V> Map<K, V> merge(Map<K, V> original, Map<K, V> extra) {
                if (original == null || original.isEmpty()) {
                    return extra;
                }
                Map<K, V> merged = new java.util.HashMap<>(original);
                merged.putAll(extra);
                return merged;
            }
        };
    }

    @RestController
    static class DemoController {
        private final ObservabilityClient client;

        DemoController(ObservabilityClient client) {
            this.client = client;
        }

        @GetMapping("/api/demo")
        ResponseEntity<String> demo(@RequestParam(name = "outcome", defaultValue = "success") String outcome) {
            String traceId = UUID.randomUUID().toString();
            String spanId = UUID.randomUUID().toString();
            Instant start = Instant.now();
            String path = "/api/demo";
            String method = "GET";

            Map<String, Object> common = Map.of(
                    "outcome", outcome,
                    "service", "observability-example",
                    "environment", "local",
                    "path", path,
                    "method", method);

            MetricEvent metric = new MetricEvent(
                    "demo.call",
                    1.0,
                    "count",
                    MetricType.COUNTER,
                    Instant.now(),
                    Map.of(
                            "outcome", outcome,
                            "service", "observability-example",
                            "environment", "local",
                            "path", path,
                            "method", method));
            client.emitMetric(metric);

            TraceSpan span = new TraceSpan(
                    spanId,
                    traceId,
                    null,
                    "demo-controller",
                    start,
                    Instant.now(),
                    common);
            client.emitTrace(span);

            StructuredLog log = new StructuredLog(
                    "fail".equalsIgnoreCase(outcome) ? LogLevel.ERROR : LogLevel.INFO,
                    "demo request",
                    Instant.now(),
                    traceId,
                    spanId,
                    common);
            client.emitLog(log);

            // enrich with duration and status and emit a second trace/log entry if needed
            Instant end = Instant.now();
            long durationMs = java.time.Duration.between(start, end).toMillis();

            Map<String, Object> enriched = Map.of(
                    "outcome", outcome,
                    "service", "observability-example",
                    "environment", "local",
                    "path", path,
                    "method", method,
                    "status", "fail".equalsIgnoreCase(outcome) ? "error" : "ok",
                    "duration_ms", durationMs);

            client.emitTrace(new TraceSpan(
                    spanId,
                    traceId,
                    null,
                    "demo-controller",
                    start,
                    end,
                    enriched));

            client.emitLog(new StructuredLog(
                    "fail".equalsIgnoreCase(outcome) ? LogLevel.ERROR : LogLevel.INFO,
                    "demo request completed",
                    end,
                    traceId,
                    spanId,
                    enriched));

            if ("fail".equalsIgnoreCase(outcome)) {
                return ResponseEntity.internalServerError().body("simulated failure");
            }
            return ResponseEntity.ok("demo ok");
        }
    }
}

