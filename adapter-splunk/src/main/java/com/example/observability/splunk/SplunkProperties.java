package com.example.observability.splunk;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "observability.splunk")
public class SplunkProperties {

    /**
     * HTTP Event Collector endpoint.
     */
    private String endpoint;

    /**
     * HEC token for authentication.
     */
    private String token;

    /**
     * Optional source value.
     */
    private String source = "unified-sdk";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

