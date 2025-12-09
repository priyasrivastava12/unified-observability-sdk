package com.example.observability.core;

/**
 * Hook to enrich events with additional attributes/tags before adapters receive them.
 * Implementations should be cheap and side-effect free.
 */
public interface ObservabilityEnricher {

    default StructuredLog enrich(StructuredLog log) {
        return log;
    }

    default MetricEvent enrich(MetricEvent metric) {
        return metric;
    }

    default TraceSpan enrich(TraceSpan span) {
        return span;
    }
}

