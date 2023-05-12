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

import java.util.List;
import java.util.Locale;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public interface IRequestStreamHandler {

    List<Locale> AVAILABLE_LOCALES = List.of(Locale.US, Locale.of("nl"), Locale.of("en")); // TODO make configurable via properties

    HandlerFunction<ServerResponse> startSessionAndBuildHTML(Route route);

    default Mono<ServerResponse> defaultRender(ServerRequest request,
                                               Route route,
                                               SecurityContext securityContext,
                                               Renderer renderer,
                                               SessionMemoryRepository sessionMemoryRepository) {
        Route.URIs.add(request.uri().getScheme() + "://" + request.uri().getAuthority());
        return ok()
                .contentType(MediaType.TEXT_HTML)
                .body(renderWithSession(request, route, renderer, sessionMemoryRepository), DataBuffer.class);
    }

    private Flux<DataBuffer> renderWithSession(ServerRequest request, Route route, Renderer renderer, SessionMemoryRepository sessionMemoryRepository) {
        final Session session = new Session(route, request);
        session.setLocale(Locale.lookup(request.headers().acceptLanguage(), AVAILABLE_LOCALES));
        final Flux<DataBuffer> render = renderer.render(route.getTemplateHTML(), session);
        session.setLastRenderedHTML(FluxUtils.dataBufferFluxToString(render));
        sessionMemoryRepository.store(session);
        return render;
    }

}
