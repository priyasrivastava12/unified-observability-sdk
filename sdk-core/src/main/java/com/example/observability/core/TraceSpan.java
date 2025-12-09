package com.example.observability.core;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class TraceSpan {

    private final String spanId;
    private final String traceId;
    private final String parentSpanId;
    private final String name;
    private final Instant startTime;
    private final Instant endTime;
    private final Map<String, Object> attributes;

    public TraceSpan(String spanId,
                     String traceId,
                     String parentSpanId,
                     String name,
                     Instant startTime,
                     Instant endTime,
                     Map<String, Object> attributes) {
        this.spanId = Objects.requireNonNull(spanId, "spanId is required");
        this.traceId = Objects.requireNonNull(traceId, "traceId is required");
        this.parentSpanId = parentSpanId;
        this.name = Objects.requireNonNull(name, "name is required");
        this.startTime = startTime == null ? Instant.now() : startTime;
        this.endTime = endTime;
        this.attributes = attributes == null ? Collections.emptyMap() : Collections.unmodifiableMap(attributes);
    }

    public String getSpanId() {
        return spanId;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public String getName() {
        return name;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}

