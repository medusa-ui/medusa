package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.render.Renderer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
@ConditionalOnClass(ReactiveSecurityContextHolder.class)
public class RequestStreamHandlerWithSecurity implements IRequestStreamHandler {

    private final Renderer renderer;
    public RequestStreamHandlerWithSecurity(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public HandlerFunction<ServerResponse> startSessionAndBuildHTML(Route route) {
        return defaultRender(renderer, route);
    }

}
