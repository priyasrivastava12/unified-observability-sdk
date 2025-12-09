package com.example.observability.splunk;

import com.example.observability.core.LogLevel;
import com.example.observability.core.StructuredLog;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SplunkAdapterTest {

    @Test
    void sendsLogToSplunkWithBearerToken() {
        var props = new SplunkProperties();
        props.setEndpoint("https://splunk.example.com/services/collector");
        props.setToken("secret-token");
        props.setSource("unified-sdk");

        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        var adapter = new SplunkAdapter(restTemplate, props);
        var log = new StructuredLog(
                LogLevel.INFO,
                "hello splunk",
                Instant.now(),
                UUID.randomUUID().toString(),
                null,
                Map.of("k", "v"));

        server.expect(once(), requestTo(props.getEndpoint()))
                .andExpect(method(POST))
                .andExpect(header("Authorization", "Bearer " + props.getToken()))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("hello splunk")))
                .andRespond(withSuccess());

        adapter.sendLog(log);
        server.verify();
    }
}

