package io.getmedusa.medusa.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(authorizeExchangeCustomizer())
                   .csrf(csrfCustomizer())
                   .build();
    }

    private static Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> authorizeExchangeCustomizer() {
        return authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll();
    }

    private static Customizer<ServerHttpSecurity.CsrfSpec> csrfCustomizer() {
        return csrfSpec -> csrfSpec.requireCsrfProtectionMatcher(csrfRequiredMatcher());
    }

    private static ServerWebExchangeMatcher csrfRequiredMatcher() {
        return exchange -> ServerWebExchangeMatchers.pathMatchers("/urls-with-csrf-check/**").matches(exchange);
    }
}
