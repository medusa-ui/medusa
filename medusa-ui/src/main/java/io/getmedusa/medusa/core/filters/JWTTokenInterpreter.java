package io.getmedusa.medusa.core.filters;

import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class JWTTokenInterpreter extends AuthenticationWebFilter {

    public JWTTokenInterpreter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);

        setServerAuthenticationConverter(exchange -> {
            List<HttpCookie> cookies = exchange.getRequest().getCookies().getOrDefault("HYDRA-SSO", new ArrayList<>());
            if(!cookies.isEmpty()) {
                String token = cookies.get(0).getValue();
                System.out.println("Incoming token: " + token);
                final UsernamePasswordAuthenticationToken data =
                        new UsernamePasswordAuthenticationToken("user", "password", List.of(new SimpleGrantedAuthority("USER")));
                return Mono.just(data);
            }

            return Mono.empty(); //TODO look up how to 'reject' properly
        });
    }
}
