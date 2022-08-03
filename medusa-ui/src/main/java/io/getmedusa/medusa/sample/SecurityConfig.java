package io.getmedusa.medusa.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .anyExchange().permitAll()
                .and().csrf().requireCsrfProtectionMatcher(
                        serverWebExchange -> ServerWebExchangeMatchers.pathMatchers("/urls-with-csrf-check/**").matches(serverWebExchange)
                )
                .and().build();
    }
}
