package com.sample.medusa.config;

import io.getmedusa.medusa.core.filters.JWTTokenInterpreter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ServerCodecConfigurer serverCodecConfigurer) {
        return http.authorizeExchange()
                .anyExchange().authenticated()
                .and().formLogin()
                .and().addFilterAfter(new JWTTokenInterpreter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

}
