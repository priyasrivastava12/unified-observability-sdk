package com.example.observability.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "observability.custom")
public class CustomPipelineProperties {

    /**
     * Whether the custom pipeline adapter is enabled.
     */
    private boolean enabled = true;

    /**
     * Logical name for logging.
     */
    private String name = "custom-pipeline";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

