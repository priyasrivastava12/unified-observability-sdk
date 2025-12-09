package com.example.observability.core;

public interface ObservabilityAdapter {

    void sendLog(StructuredLog log);

    void sendMetric(MetricEvent metric);

    void sendTrace(TraceSpan span);

    default void flush() {
        // optional
    }

    default void shutdown() {
        // optional
    }
}

