package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.SecurityContext;
import io.getmedusa.medusa.core.session.Session;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public interface IRequestStreamHandler {

    HandlerFunction<ServerResponse> startSessionAndBuildHTML(Route route);

    default Mono<ServerResponse> defaultRender(ServerRequest request,
                                               Route route,
                                               SecurityContext securityContext,
                                               Renderer renderer,
                                               SessionMemoryRepository sessionMemoryRepository) {
        return ok().contentType(MediaType.TEXT_HTML).bodyValue(renderWithSession(request, route, renderer, sessionMemoryRepository));
    }

    private String renderWithSession(ServerRequest request, Route route, Renderer renderer, SessionMemoryRepository sessionMemoryRepository) {
        final Session session = new Session(route, request);
        final String render = renderer.render(route.getTemplateHTML(), session);
        session.setLastRenderedHTML(render);
        sessionMemoryRepository.store(session);
        return render;
    }

}
