package io.getmedusa.medusa.core.router;

import io.getmedusa.medusa.core.registry.ActiveSessionRegistry;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static io.getmedusa.medusa.core.injector.HTMLInjector.INSTANCE;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * Standard implementation of the {@link IRequestStreamHandler}. This class will be used only when there is a Spring Security library present in the client.
 * As such, the passed along security context is always built up from the current authentication. Otherwise, its implementation is identical to {@link RequestStreamHandler}
 */
@Component
@ConditionalOnClass(ReactiveSecurityContextHolder.class)
class RequestStreamHandlerWithSecurity implements IRequestStreamHandler {

    @Override
    public HandlerFunction<ServerResponse> handle(String script, String styling, String fileName) {
        return request -> {
            Mono<CsrfToken> tokenEntry = request.exchange().getAttributes().entrySet().stream().filter(e -> e.getKey().toLowerCase().contains("csrf")).map(e -> (Mono<CsrfToken>) e.getValue()).findFirst().orElse(Mono.empty());
            return tokenEntry.flatMap(csrfToken -> {
                if(fileName.equals("pages/login")) {
                    final SecurityContext securityContext = new SecurityContext(null);
                    ActiveSessionRegistry.getInstance().registerSecurityContext(securityContext);
                    return ok().contentType(MediaType.TEXT_HTML).bodyValue(INSTANCE.inject(request, securityContext, fileName, script, styling, csrfToken.getToken()));

                } else {
                    return ReactiveSecurityContextHolder.getContext().switchIfEmpty(Mono.just(new EmptySecurityContext())).flatMap(authenticationHolder ->
                            {
                                try {
                                    final SecurityContext securityContext = new SecurityContext(authenticationHolder.getAuthentication());
                                    ActiveSessionRegistry.getInstance().registerSecurityContext(securityContext);
                                    return ok().contentType(MediaType.TEXT_HTML).bodyValue(INSTANCE.inject(request, securityContext, fileName, script, styling, csrfToken.getToken()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return Mono.error(e);
                                }
                            }
                    );
                }
            });
        };
    }

    private class EmptySecurityContext implements org.springframework.security.core.context.SecurityContext {

        @Override
        public Authentication getAuthentication() {
            return null;
        }

        @Override
        public void setAuthentication(Authentication authentication) {

        }
    }
}
