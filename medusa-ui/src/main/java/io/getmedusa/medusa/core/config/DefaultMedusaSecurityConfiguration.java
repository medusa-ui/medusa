package io.getmedusa.medusa.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * This configuration will only be loaded when no {@link org.springframework.security.web.server.SecurityWebFilterChain} is provided.
 * By default, nothing is restricted (permitAll).
 */
@ConditionalOnMissingBean(SecurityWebFilterChain.class)
@Configuration
public class DefaultMedusaSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .build();
    }
}
