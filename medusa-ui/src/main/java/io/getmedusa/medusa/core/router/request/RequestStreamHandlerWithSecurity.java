package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.router.request.meta.EmptySpringSecurityContext;
import io.getmedusa.medusa.core.session.SecurityContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Standard implementation of the {@link IRequestStreamHandler}. This class will be used only when there is a Spring Security library present in the client.
 * As such, the passed along security context is always built up from the current authentication. Otherwise, its implementation is identical to {@link RequestStreamHandler}
 */
@Component
@ConditionalOnClass(ReactiveSecurityContextHolder.class)
public class RequestStreamHandlerWithSecurity implements IRequestStreamHandler {

    private final Renderer renderer;
    private final SessionMemoryRepository sessionMemoryRepository;

    public RequestStreamHandlerWithSecurity(Renderer renderer, SessionMemoryRepository sessionMemoryRepository) {
        this.renderer = renderer;
        this.sessionMemoryRepository = sessionMemoryRepository;
    }

    @Override
    public HandlerFunction<ServerResponse> startSessionAndBuildHTML(Route route) {
        return request -> ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.just(new EmptySpringSecurityContext()))
                .flatMap(a -> defaultRender(request, route, new SecurityContext(a.getAuthentication()), renderer, sessionMemoryRepository));
    }

}
