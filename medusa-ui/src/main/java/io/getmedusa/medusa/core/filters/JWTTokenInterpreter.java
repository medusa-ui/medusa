package io.getmedusa.medusa.core.filters;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

public class JWTTokenInterpreter extends AuthenticationWebFilter {

    private static final String BEARER = "Bearer ";
    private static final String AUTH = "Authorization";

    public JWTTokenInterpreter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);

        setServerAuthenticationConverter(exchange -> {
            List<String> tokens = exchange.getRequest().getHeaders().get(AUTH);
            if(tokens == null || tokens.isEmpty()) return Mono.empty();

            String token = tokens.get(0).replace(BEARER, "");
            System.out.println("Token: " + token);

            return Mono.just(new UsernamePasswordAuthenticationToken("user", "password"));
        });
    }
}
