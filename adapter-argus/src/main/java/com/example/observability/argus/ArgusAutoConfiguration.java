package com.example.observability.argus;

import com.example.observability.core.ObservabilityAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(ArgusProperties.class)
@ConditionalOnProperty(prefix = "observability.argus", name = "enabled", havingValue = "true")
public class ArgusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate argusRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(name = "argusObservabilityAdapter")
    public ObservabilityAdapter argusObservabilityAdapter(RestTemplate restTemplate, ArgusProperties properties) {
        return new ArgusAdapter(restTemplate, properties);
    }
}

