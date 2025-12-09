package com.example.observability.custom;

import com.example.observability.core.MetricEvent;
import com.example.observability.core.ObservabilityAdapter;
import com.example.observability.core.StructuredLog;
import com.example.observability.core.TraceSpan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@EnableConfigurationProperties(CustomPipelineProperties.class)
@ConditionalOnProperty(prefix = "observability.custom", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CustomPipelineAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "customObservabilityAdapter")
    public ObservabilityAdapter customObservabilityAdapter(CustomPipelineProperties properties,
                                                           ObjectProvider<Consumer<StructuredLog>> logHandler,
                                                           ObjectProvider<Consumer<MetricEvent>> metricHandler,
                                                           ObjectProvider<Consumer<TraceSpan>> traceHandler) {
        return new CustomPipelineAdapter(
                properties.getName(),
                logHandler.getIfAvailable(),
                metricHandler.getIfAvailable(),
                traceHandler.getIfAvailable());
    }
}

