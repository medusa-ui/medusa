package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.render.Renderer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
@ConditionalOnMissingBean(RequestStreamHandlerWithSecurity.class)
public class RequestStreamHandler implements IRequestStreamHandler {

    private final Renderer renderer;
    public RequestStreamHandler(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public HandlerFunction<ServerResponse> startSessionAndBuildHTML(Route route) {
        //final SecurityContext securityContext = new SecurityContext(null);
        return defaultRender(renderer, route);
    }

}
