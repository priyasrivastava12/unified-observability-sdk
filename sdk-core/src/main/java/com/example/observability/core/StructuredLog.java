package com.example.observability.core;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class StructuredLog {

    private final LogLevel level;
    private final String message;
    private final Instant timestamp;
    private final String traceId;
    private final String spanId;
    private final Map<String, Object> attributes;

    public StructuredLog(LogLevel level,
                         String message,
                         Instant timestamp,
                         String traceId,
                         String spanId,
                         Map<String, Object> attributes) {
        this.level = Objects.requireNonNull(level, "level is required");
        this.message = Objects.requireNonNull(message, "message is required");
        this.timestamp = timestamp == null ? Instant.now() : timestamp;
        this.traceId = traceId;
        this.spanId = spanId;
        this.attributes = attributes == null ? Collections.emptyMap() : Collections.unmodifiableMap(attributes);
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}

