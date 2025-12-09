package com.example.observability.argus;

import com.example.observability.core.MetricEvent;
import com.example.observability.core.MetricType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ArgusAdapterTest {

    @Test
    void sendsMetricToArgusWithBearerToken() {
        var props = new ArgusProperties();
        props.setEndpoint("https://argus.example.com/ingest");
        props.setToken("argus-token");

        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        var adapter = new ArgusAdapter(restTemplate, props);
        var metric = new MetricEvent("requests", 2.0, "count", MetricType.COUNTER, Instant.now(), Map.of("route", "/health"));

        server.expect(once(), requestTo(props.getEndpoint()))
                .andExpect(method(POST))
                .andExpect(header("Authorization", "Bearer " + props.getToken()))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("requests")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("metric")))
                .andRespond(withSuccess());

        adapter.sendMetric(metric);
        server.verify();
    }
}

