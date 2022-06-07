package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.SecurityContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Standard implementation of the {@link IRequestStreamHandler}. This class will be used only when there is no Spring Security present in the client.
 * As such, the passed along security context is always null.
 */
@Component
@ConditionalOnMissingBean(RequestStreamHandlerWithSecurity.class)
public class RequestStreamHandler implements IRequestStreamHandler {

    private final Renderer renderer;
    private final SessionMemoryRepository sessionMemoryRepository;

    public RequestStreamHandler(Renderer renderer, SessionMemoryRepository sessionMemoryRepository) {
        this.renderer = renderer;
        this.sessionMemoryRepository = sessionMemoryRepository;
    }

    @Override
    public HandlerFunction<ServerResponse> startSessionAndBuildHTML(Route route) {
        return request -> defaultRender(request, route, new SecurityContext(null), renderer, sessionMemoryRepository);
    }

}
