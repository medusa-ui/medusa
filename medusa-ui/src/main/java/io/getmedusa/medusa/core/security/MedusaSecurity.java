package io.getmedusa.medusa.core.security;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;

/**
 * This class provides a default setup for Spring Security with Medusa.
 * It predefines form login and adds a JWT Token interpretation filter to facilitate Hydra logins.
 * You only need to do this if you have protected endpoints, otherwise a default SecurityWebFilterChain sets everything up for you.
 */
public final class MedusaSecurity {

    private MedusaSecurity() {}

    public static ServerHttpSecurity defaultSecurity(ServerHttpSecurity http) {
        return http.addFilterBefore(new JWTTokenInterpreter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .formLogin(Customizer.withDefaults())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
    }

}
