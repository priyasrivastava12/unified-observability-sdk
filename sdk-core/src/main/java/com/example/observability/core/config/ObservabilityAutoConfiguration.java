package com.example.observability.core.config;

import com.example.observability.core.ObservabilityAdapter;
import com.example.observability.core.ObservabilityClient;
import com.example.observability.core.ObservabilityEnricher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(ObservabilityProperties.class)
@ConditionalOnProperty(prefix = "observability", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ObservabilityAutoConfiguration {

    @Bean
    public ObservabilityClient observabilityClient(List<ObservabilityAdapter> adapters,
                                                   ObjectProvider<List<ObservabilityEnricher>> enrichersProvider) {
        return new ObservabilityClient(adapters, enrichersProvider.getIfAvailable());
    }
}

