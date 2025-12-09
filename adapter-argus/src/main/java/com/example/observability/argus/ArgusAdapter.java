package com.example.observability.argus;

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

public class ArgusAdapter implements ObservabilityAdapter {

    private static final Logger log = LoggerFactory.getLogger(ArgusAdapter.class);

    private final RestTemplate restTemplate;
    private final ArgusProperties properties;

    public ArgusAdapter(RestTemplate restTemplate, ArgusProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        Assert.hasText(properties.getEndpoint(), "Argus endpoint must be set");
        Assert.hasText(properties.getToken(), "Argus token must be set");
    }

    @Override
    public void sendLog(StructuredLog logEvent) {
        post("log", logEvent);
    }

    @Override
    public void sendMetric(MetricEvent metric) {
        post("metric", metric);
    }

    @Override
    public void sendTrace(TraceSpan span) {
        post("trace", span);
    }

    private void post(String type, Object payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getToken());
            Map<String, Object> body = new HashMap<>();
            body.put("type", type);
            body.put("data", payload);
            restTemplate.postForEntity(properties.getEndpoint(), new HttpEntity<>(body, headers), String.class);
        } catch (Exception ex) {
            log.warn("Failed to publish {} to Argus: {}", type, ex.getMessage());
        }
    }
}

