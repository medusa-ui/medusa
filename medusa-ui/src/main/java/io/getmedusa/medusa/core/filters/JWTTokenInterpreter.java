package io.getmedusa.medusa.core.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.getmedusa.medusa.core.websocket.hydra.HydraConnection;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JWTTokenInterpreter extends AuthenticationWebFilter {

    static Cache<String, UsernamePasswordAuthenticationToken> cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(250)
            .build();

    public JWTTokenInterpreter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);

        setServerAuthenticationConverter(exchange -> {
            List<HttpCookie> cookies = exchange.getRequest().getCookies().getOrDefault("HYDRA-SSO", new ArrayList<>());
            if (!cookies.isEmpty()) {
                String token = cookies.get(0).getValue();
                UsernamePasswordAuthenticationToken auth = cache.get(token, t -> {
                    final String tokenPayload = verifyToken(token);
                    if (null == tokenPayload) return null;
                    return new UsernamePasswordAuthenticationToken("user", "password", List.of(new SimpleGrantedAuthority("USER")));
                });
                if (auth == null) return reject();
                return Mono.just(auth);
            }
            return reject();
        });
    }

    private Mono<Authentication> reject() {
        return Mono.empty();
    }

    private String verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.RSA256(HydraConnection.publicKey, null))
                    .withIssuer("hydra")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            if (jwt == null) return null;
            return jwt.getClaim("username").asString();
        } catch (Exception exception) {
            return null;
        }
    }
}
