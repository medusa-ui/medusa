package sample.getmedusa.showcase;

import io.getmedusa.medusa.core.security.JWTTokenInterpreter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

//this config is required for now, but should be not required
@Configuration
@EnableWebFluxSecurity
public class SecurityConfigToDelete {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.addFilterBefore(new JWTTokenInterpreter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .formLogin().and()
                .httpBasic().disable()
                .authorizeExchange()
                //.anyExchange().authenticated()
                .anyExchange().permitAll()
                .and().csrf().requireCsrfProtectionMatcher(
                    serverWebExchange -> ServerWebExchangeMatchers.pathMatchers("/urls-with-csrf-check/**").matches(serverWebExchange)
                )
                .and().build();
    }



}
