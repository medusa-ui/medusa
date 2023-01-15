package io.getmedusa.medusa.core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class JWTTokenInterpreter extends AuthenticationWebFilter {

    public static RSAPublicKey PUBLIC_KEY = null;
    private static final Map<String, String> ROLE_MAPPING = new HashMap<>();

    static Cache<String, Authentication> cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(250)
            .build();

    public static void clearCache() {
        cache.invalidateAll();
    }

    public JWTTokenInterpreter() {
        super(new KnownAuthenticationManager());

        setServerAuthenticationConverter(exchange -> {
            List<HttpCookie> cookies = exchange.getRequest().getCookies().getOrDefault("HYDRA-SSO", new ArrayList<>());
            if (!cookies.isEmpty()) {
                String token = cookies.get(0).getValue();
                Authentication auth = cache.get(token, t -> verifyToken(token));
                if (auth == null) return reject(exchange);
                return Mono.just(auth);
            }
            return reject(exchange);
        });
    }

    public static void handleUpdate(String pubKeyAsText, Map<String, String> roleMappings) {
        if(null != pubKeyAsText) {
            ROLE_MAPPING.clear();
            try {
                System.out.println("Loading new Hydra public key");
                byte[] decodedKey = Base64.getDecoder().decode(pubKeyAsText);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PUBLIC_KEY = (RSAPublicKey) kf.generatePublic(spec);

                ROLE_MAPPING.putAll(roleMappings);
                System.out.println("Hydra connection established with public key");
            } catch (Exception e) {
                PUBLIC_KEY = null;
                throw new RuntimeException(e);
            } finally {
                JWTTokenInterpreter.clearCache();
            }
        }
    }

    private Mono<Authentication> reject(ServerWebExchange exchange) {
        String requestedPath = exchange.getRequest().getPath().toString();
        exchange.getResponse().addCookie(ResponseCookie.from("Referer", requestedPath).httpOnly(true).maxAge(Duration.ofMinutes(4)).build());
        return Mono.empty();
    }

    private PreAuthenticatedAuthenticationToken verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.RSA256(PUBLIC_KEY, null))
                    .withIssuer("hydra")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            if (jwt == null) return null;

            final String username = jwt.getClaim("username").asString();
            final String[] roles = jwt.getClaim("roles").asArray(String.class);
            List<SimpleGrantedAuthority> authorities = buildAuthorities(mapRolesIfApplicable(roles));
            final PreAuthenticatedAuthenticationToken t = new PreAuthenticatedAuthenticationToken(username, new SecureRandom(), authorities);
            t.setAuthenticated(true);
            return t;
        } catch (Exception exception) {
            return null;
        }
    }

    private static List<String> mapRolesIfApplicable(String[] roles) {
        List<String> mappedRoles = new ArrayList<>();
        for(String r : roles) {
            mappedRoles.add(ROLE_MAPPING.getOrDefault(r, r));
        }
        return mappedRoles;
    }

    private List<SimpleGrantedAuthority> buildAuthorities(List<String> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if(roles.isEmpty()) return authorities;
        for(String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.toUpperCase()));
        }
        return authorities;
    }
}
