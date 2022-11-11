package io.getmedusa.medusa.core.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class DefaultSecurityConfiguration {

    @ConditionalOnMissingBean(SecurityWebFilterChain.class)
    @Bean
    public SecurityWebFilterChain permitAllSecurityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .anyExchange().permitAll()
                .and().build();
    }
}
