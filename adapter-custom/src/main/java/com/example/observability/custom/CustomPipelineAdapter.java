package com.example.observability.custom;

import com.example.observability.core.MetricEvent;
import com.example.observability.core.ObservabilityAdapter;
import com.example.observability.core.StructuredLog;
import com.example.observability.core.TraceSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class CustomPipelineAdapter implements ObservabilityAdapter {

    private static final Logger log = LoggerFactory.getLogger(CustomPipelineAdapter.class);

    private final String name;
    private final Consumer<StructuredLog> logHandler;
    private final Consumer<MetricEvent> metricHandler;
    private final Consumer<TraceSpan> traceHandler;

    public CustomPipelineAdapter(String name,
                                 Consumer<StructuredLog> logHandler,
                                 Consumer<MetricEvent> metricHandler,
                                 Consumer<TraceSpan> traceHandler) {
        this.name = name;
        this.logHandler = logHandler;
        this.metricHandler = metricHandler;
        this.traceHandler = traceHandler;
    }

    @Override
    public void sendLog(StructuredLog logEvent) {
        if (logHandler != null) {
            logHandler.accept(logEvent);
            return;
        }
        log.debug("Custom pipeline {} received log: {}", name, logEvent.getMessage());
    }

    @Override
    public void sendMetric(MetricEvent metric) {
        if (metricHandler != null) {
            metricHandler.accept(metric);
            return;
        }
        log.debug("Custom pipeline {} received metric {}", name, metric.getName());
    }

    @Override
    public void sendTrace(TraceSpan span) {
        if (traceHandler != null) {
            traceHandler.accept(span);
            return;
        }
        log.debug("Custom pipeline {} received trace {}", name, span.getSpanId());
    }
}

