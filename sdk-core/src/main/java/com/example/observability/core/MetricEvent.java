package com.example.observability.core;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class MetricEvent {

    private final String name;
    private final double value;
    private final String unit;
    private final MetricType type;
    private final Instant timestamp;
    private final Map<String, String> tags;

    public MetricEvent(String name,
                       double value,
                       String unit,
                       MetricType type,
                       Instant timestamp,
                       Map<String, String> tags) {
        this.name = Objects.requireNonNull(name, "name is required");
        this.value = value;
        this.unit = unit;
        this.type = type == null ? MetricType.COUNTER : type;
        this.timestamp = timestamp == null ? Instant.now() : timestamp;
        this.tags = tags == null ? Collections.emptyMap() : Collections.unmodifiableMap(tags);
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public MetricType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getTags() {
        return tags;
    }
}

