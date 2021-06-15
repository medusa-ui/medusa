package io.getmedusa.medusa.core.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(session.receive()
                        .map(msg -> interpretEvent(msg.getPayloadAsText()))
                        .map(session::textMessage));
    }

    private String interpretEvent(final String event) {
        System.out.println("Interpret: " + event);
        String domChange = UUID.randomUUID().toString();
        return domChange;
    }

}
