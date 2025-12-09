package com.example.observability.splunk;

import com.example.observability.core.MetricEvent;
import com.example.observability.core.ObservabilityAdapter;
import com.example.observability.core.StructuredLog;
import com.example.observability.core.TraceSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class SplunkAdapter implements ObservabilityAdapter {

    private static final Logger log = LoggerFactory.getLogger(SplunkAdapter.class);

    private final RestTemplate restTemplate;
    private final SplunkProperties properties;

    public SplunkAdapter(RestTemplate restTemplate, SplunkProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        Assert.hasText(properties.getEndpoint(), "Splunk endpoint must be set");
        Assert.hasText(properties.getToken(), "Splunk token must be set");
    }

    @Override
    public void sendLog(StructuredLog structuredLog) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("time", structuredLog.getTimestamp().getEpochSecond());
        payload.put("host", "application");
        payload.put("source", properties.getSource());
        payload.put("event", structuredLog);
        post(payload);
    }

    @Override
    public void sendMetric(MetricEvent metric) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("time", metric.getTimestamp().getEpochSecond());
        payload.put("host", "application");
        payload.put("source", properties.getSource());
        payload.put("event", metric);
        post(payload);
    }

    @Override
    public void sendTrace(TraceSpan span) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("time", span.getStartTime().getEpochSecond());
        payload.put("host", "application");
        payload.put("source", properties.getSource());
        payload.put("event", span);
        post(payload);
    }

    private void post(Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getToken());
            restTemplate.postForEntity(properties.getEndpoint(), new HttpEntity<>(payload, headers), String.class);
        } catch (Exception ex) {
            log.warn("Failed to publish event to Splunk: {}", ex.getMessage());
        }
    }
}

