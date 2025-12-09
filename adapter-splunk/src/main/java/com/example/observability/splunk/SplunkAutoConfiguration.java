package com.example.observability.splunk;

import com.example.observability.core.ObservabilityAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(SplunkProperties.class)
@ConditionalOnProperty(prefix = "observability.splunk", name = "enabled", havingValue = "true")
public class SplunkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate splunkRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(name = "splunkObservabilityAdapter")
    public ObservabilityAdapter splunkObservabilityAdapter(RestTemplate restTemplate, SplunkProperties properties) {
        return new SplunkAdapter(restTemplate, properties);
    }
}

