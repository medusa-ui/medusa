package io.getmedusa.medusa.core.router.request;

import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.SecurityContext;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public interface IRequestStreamHandler {

    HandlerFunction<ServerResponse> startSessionAndBuildHTML(Route route);

    default Mono<ServerResponse> defaultRender(ServerRequest request,
                                               Route route,
                                               SecurityContext securityContext,
                                               Renderer renderer,
                                               SessionMemoryRepository sessionMemoryRepository) {
        Route.URIs.add(request.uri().getScheme() + "://" + request.uri().getAuthority());

        final Session session = new Session(route, request);
        return route.getSetupAttributes(request, session).flatMap(sAttributes -> {
            session.setLastParameters(sAttributes);
            Mono<DataBuffer> bufferMono = DataBufferUtils.join(renderer.render(route.getTemplateHTML(), session));
            return bufferMono.flatMap(r -> {
                String renderedHTML = FluxUtils.dataBufferToString(r);
                session.setLastRenderedHTML(renderedHTML);
                sessionMemoryRepository.store(session);
                return ok().contentType(MediaType.TEXT_HTML).body(Mono.just(renderedHTML), String.class);
            });
        });
    }

}
