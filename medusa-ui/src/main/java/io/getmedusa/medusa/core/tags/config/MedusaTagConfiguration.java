package io.getmedusa.medusa.core.tags.config;

import io.getmedusa.medusa.core.tags.MedusaDialect;
import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class MedusaTagConfiguration {

    @Bean
    public AbstractProcessorDialect medusaDialect(ApplicationContext context) {
        Set<IProcessor> medusaTags = context.getBeansWithAnnotation(MedusaTag.class)
                .values()
                .stream().map(IProcessor.class::cast)
                .collect(Collectors.toSet());

        return new MedusaDialect( medusaTags );
    }
}