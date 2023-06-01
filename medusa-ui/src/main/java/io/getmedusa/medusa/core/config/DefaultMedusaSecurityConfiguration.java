package io.getmedusa.medusa.core.config;

import io.getmedusa.medusa.core.security.MedusaSecurity;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * This configuration will only be loaded when no {@link org.springframework.security.web.server.SecurityWebFilterChain} is provided.
 * By default, nothing is restricted (permitAll).
 */
@ConditionalOnMissingBean(SecurityWebFilterChain.class)
@AutoConfiguration
public class DefaultMedusaSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return MedusaSecurity.defaultSecurity(http)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll())
                .build();
    }

}
