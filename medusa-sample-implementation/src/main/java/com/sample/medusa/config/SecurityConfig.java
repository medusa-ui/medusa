package com.sample.medusa.config;

import io.getmedusa.medusa.core.filters.JWTTokenInterpreter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ServerCodecConfigurer serverCodecConfigurer) {
        return http.authorizeExchange()
                .anyExchange().authenticated()
                .and().formLogin()
                .and().addFilterAfter(new JWTTokenInterpreter(authenticationManager()), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        authenticationManager.setPasswordEncoder(passwordEncoder());

        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CachedBCryptEncoder();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}
