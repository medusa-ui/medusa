package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.injector.DOMChanges.DOMChange;
import io.getmedusa.medusa.core.registry.*;
import io.getmedusa.medusa.core.util.ExpressionEval;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import io.getmedusa.medusa.core.util.ObjectMapperBuilder;

import java.util.*;

/**
 * Handles the lifecycle of the websocket session
 * Mostly contains logic on what to do when an event arrives serverside
 */
@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    public static final ObjectMapper MAPPER = ObjectMapperBuilder.setupObjectMapper();
    private static final DomChangesExecution DOM_CHANGES_EXECUTION = new DomChangesExecution();

    private final String hydraURL;
    public ReactiveWebSocketHandler(@Value("${hydra.url:}") String hydraURL) {
        this.hydraURL = (hydraURL.isBlank()) ? null : hydraURL;
    }

    /**
     * Mono impl of the entire websocket session lifecycle. We send a response event (all changes due to event) to any event we receive.
     * Spring Reactive knows to kill the session as appropriate, which in turn will trigger the doFinally()
     * @param session active websocket session
     * @return full mono impl
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        ActiveSessionRegistry.getInstance().add(session);
        securityCheckForOrigin(session);

        return session.send(session.receive()
                        .map(msg -> interpretEvent(session, msg.getPayloadAsText()))
                        .map(session::textMessage).doFinally(sig -> ActiveSessionRegistry.getInstance().remove(session)));
    }

    private void securityCheckForOrigin(WebSocketSession session) {
        final HandshakeInfo info = session.getHandshakeInfo();
        final List<String> origin = info.getHeaders().get("Origin");
        if(origin == null || origin.isEmpty()) throw new SecurityException("Missing origin");
        final String sessionOrigin = origin.get(0);
        if(!info.getUri().toString().startsWith(sessionOrigin) &&
                !sessionOrigin.replace("http://", "").replace("https://", "").equals(this.hydraURL)) {
            throw new SecurityException("Illegal origin");
        }
    }

    /**
     * Chain of actions to trigger on an incoming event
     * @param session active websocket session
     * @param event incoming event as an unparsed string()
     * @return JSON version of all changes due to the event, to be sent as a websocket response
     */
    private String interpretEvent(final WebSocketSession session, final String event) {
        try {
            if(event.startsWith("unq//")) {
                String uniqueSecurityId = event.replace("unq//", "");
                ActiveSessionRegistry.getInstance().associateSecurityContext(uniqueSecurityId, session);
                return "unq//ok";
            } else {
                List<DOMChange> domChanges = DOM_CHANGES_EXECUTION.process(session, executeEvent(session, event));
                return MAPPER.writeValueAsString(domChanges);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute incoming event as a SpelExpression on the current UIEventWithAttributes the WebSocketSession is attached to
     * @param session active websocket session
     * @param event incoming event as an unparsed string()
     * @return list of changes
     */
    private List<DOMChange> executeEvent(WebSocketSession session, String event) {
        try {
            List<DOMChange> domChanges = new ArrayList<>();
            final UIEventController eventController = EventHandlerRegistry.getInstance().get(session);
            event = EventOptionalParams.rebuildEventWithOptionalParams(eventController.getEventHandler().getClass(), session, event);
            final DOMChanges domChangesBuilder = ExpressionEval.evalEventController(event, eventController.getEventHandler());
            final List<DOMChange> parsedExpressionValues = domChangesBuilder.build();
            if (parsedExpressionValues != null) domChanges = new ArrayList<>(parsedExpressionValues);
            return domChanges;
        } catch (SpelEvaluationException e) {
            throw new IllegalArgumentException("Event '" + event + "' could not be executed", e);
        }
    }
}
