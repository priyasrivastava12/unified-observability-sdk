package com.example.observability.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObservabilityClient {

    private final List<ObservabilityAdapter> adapters = new CopyOnWriteArrayList<>();
    private final List<ObservabilityEnricher> enrichers = new CopyOnWriteArrayList<>();

    public ObservabilityClient(List<ObservabilityAdapter> adapters) {
        if (adapters != null) {
            this.adapters.addAll(adapters);
        }
    }

    public ObservabilityClient(List<ObservabilityAdapter> adapters, List<ObservabilityEnricher> enrichers) {
        if (adapters != null) {
            this.adapters.addAll(adapters);
        }
        if (enrichers != null) {
            this.enrichers.addAll(enrichers);
        }
    }

    public void registerAdapter(ObservabilityAdapter adapter) {
        adapters.add(Objects.requireNonNull(adapter));
    }

    public void emitLog(StructuredLog log) {
        StructuredLog enriched = applyLogEnrichers(log);
        adapters.forEach(adapter -> adapter.sendLog(enriched));
    }

    public void emitMetric(MetricEvent metric) {
        MetricEvent enriched = applyMetricEnrichers(metric);
        adapters.forEach(adapter -> adapter.sendMetric(enriched));
    }

    public void emitTrace(TraceSpan span) {
        TraceSpan enriched = applyTraceEnrichers(span);
        adapters.forEach(adapter -> adapter.sendTrace(enriched));
    }

    public void flush() {
        adapters.forEach(ObservabilityAdapter::flush);
    }

    public void shutdown() {
        adapters.forEach(ObservabilityAdapter::shutdown);
    }

    private StructuredLog applyLogEnrichers(StructuredLog log) {
        StructuredLog current = log;
        for (ObservabilityEnricher enricher : enrichers) {
            current = enricher.enrich(current);
        }
        return current;
    }

    private MetricEvent applyMetricEnrichers(MetricEvent metric) {
        MetricEvent current = metric;
        for (ObservabilityEnricher enricher : enrichers) {
            current = enricher.enrich(current);
        }
        return current;
    }

    private TraceSpan applyTraceEnrichers(TraceSpan span) {
        TraceSpan current = span;
        for (ObservabilityEnricher enricher : enrichers) {
            current = enricher.enrich(current);
        }
        return current;
    }
}

