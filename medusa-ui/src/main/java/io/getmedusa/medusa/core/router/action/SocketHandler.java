package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.boot.RouteDetection;
import io.getmedusa.medusa.core.diffengine.DiffEngine;
import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

//event emitter endpoint that rsocket can connect to

@Controller
public class SocketHandler {

    private final SessionMemoryRepository sessionMemoryRepository;
    private final ActionHandler actionHandler;
    private final Renderer renderer;
    private final DiffEngine diffEngine;

    public SocketHandler(SessionMemoryRepository sessionMemoryRepository,
                         ActionHandler actionHandler,
                         Renderer renderer,
                         DiffEngine diffEngine){
        this.sessionMemoryRepository = sessionMemoryRepository;
        this.actionHandler = actionHandler;
        this.renderer = renderer;
        this.diffEngine = diffEngine;
    }

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("event-emitter/{hash}/{sessionId}")
    public Flux<List<JSReadyDiff>> eventEmitter(final @Headers Map<String, Object> metadata,
                                                final @Payload Flux<SocketAction> request,
                                                final @DestinationVariable String hash,
                                                final @DestinationVariable String sessionId) {

        final Route route = RouteDetection.INSTANCE.findRoute(hash);

        //retrieve session
        //TODO can this be done via metadata/a more secure way?
        final Session session = sessionMemoryRepository.retrieve(sessionId, route);

        return request.flatMap(r -> {
            //execute action and combine attributes
            Session updatedSession = actionHandler.executeAndMerge(r, route, session);

            //render new HTML w/ new attributes
            final Flux<DataBuffer> dataBufferFlux = renderer.render(route.getTemplateHTML(), updatedSession);
            final String oldHTML = updatedSession.getLastRenderedHTML();
            final String newHtml = FluxUtils.dataBufferFluxToString(dataBufferFlux);
            updatedSession.setLastRenderedHTML(newHtml);
            sessionMemoryRepository.store(updatedSession);

            //run diff engine old HTML vs new
            return diffEngine.findDiffs(oldHTML, newHtml);
        });
    }

}