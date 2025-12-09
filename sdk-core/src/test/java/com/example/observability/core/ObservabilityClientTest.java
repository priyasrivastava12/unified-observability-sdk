package com.example.observability.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObservabilityClientTest {

    @Test
    void sendsEventsToAllAdapters() {
        var receivedLogs = new ArrayList<StructuredLog>();
        var receivedMetrics = new ArrayList<MetricEvent>();
        var receivedSpans = new ArrayList<TraceSpan>();

        ObservabilityAdapter captureAdapter = new ObservabilityAdapter() {
            @Override
            public void sendLog(StructuredLog log) {
                receivedLogs.add(log);
            }

            @Override
            public void sendMetric(MetricEvent metric) {
                receivedMetrics.add(metric);
            }

            @Override
            public void sendTrace(TraceSpan span) {
                receivedSpans.add(span);
            }
        };

        var client = new ObservabilityClient(List.of(captureAdapter));

        var log = new StructuredLog(LogLevel.INFO, "hello", Instant.now(), "trace-1", "span-1", Map.of());
        var metric = new MetricEvent("requests", 1.0, "count", MetricType.COUNTER, Instant.now(), Map.of("route", "/ping"));
        var span = new TraceSpan(UUID.randomUUID().toString(), "trace-1", null, "op", Instant.now(), Instant.now(), Map.of("status", "ok"));

        client.emitLog(log);
        client.emitMetric(metric);
        client.emitTrace(span);

        assertThat(receivedLogs).containsExactly(log);
        assertThat(receivedMetrics).containsExactly(metric);
        assertThat(receivedSpans).containsExactly(span);
    }
}

