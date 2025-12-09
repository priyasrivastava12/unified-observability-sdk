package com.example.observability.argus;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "observability.argus")
public class ArgusProperties {

    /**
     * Argus endpoint for metric ingestion.
     */
    private String endpoint;

    /**
     * API key or bearer token.
     */
    private String token;

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
}

