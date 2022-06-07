package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public interface IRequestStreamHandler {

    HandlerFunction<ServerResponse> startSessionAndBuildHTML(Route route);

    default HandlerFunction<ServerResponse> defaultRender(Renderer renderer, Route route) {
        return request -> ok()
                .contentType(MediaType.TEXT_HTML)
                .bodyValue(renderer.render(route.getTemplateHTML(), new Session(route, request)));
    }

}
