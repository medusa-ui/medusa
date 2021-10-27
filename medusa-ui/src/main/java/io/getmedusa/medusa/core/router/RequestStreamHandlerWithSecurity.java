package io.getmedusa.medusa.core.router;

import io.getmedusa.medusa.core.registry.ActiveSessionRegistry;
import io.getmedusa.medusa.core.util.SecurityContext;
import io.getmedusa.medusa.core.websocket.EventOptionalParams;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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
        EventOptionalParams.securityEnabled = true;
        return request -> ReactiveSecurityContextHolder.getContext().flatMap(authenticationHolder ->
                {
                    try {
                        final SecurityContext securityContext = new SecurityContext(authenticationHolder.getAuthentication());
                        ActiveSessionRegistry.getInstance().registerSecurityContext(securityContext);
                        return ok().contentType(MediaType.TEXT_HTML).bodyValue(INSTANCE.inject(request, securityContext, fileName, script, styling));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Mono.error(e);
                    }
                }
        );
    }

}
