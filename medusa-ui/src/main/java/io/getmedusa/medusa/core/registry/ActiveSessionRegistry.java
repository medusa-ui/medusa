package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.util.WebsocketMessageUtils;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class ActiveSessionRegistry {

    private static final ActiveSessionRegistry INSTANCE = new ActiveSessionRegistry();

    private ActiveSessionRegistry() { }
    public static ActiveSessionRegistry getInstance() {
        return INSTANCE;
    }

    private final List<WebSocketSession> registry = new ArrayList<>();

    public void add(WebSocketSession session) {
        if(null == session) return;
        System.out.println("Start session: " + session.getId());
        registry.add(session);
    }

    public void remove(WebSocketSession session) {
        if(null == session) return;
        System.out.println("END of session : " + session.getId());
        registry.remove(session);
    }

    private List<WebSocketSession> getAllSessions() {
        return registry;
    }

    public void sendToAll(Object objToSend) {
        Flux<WebSocketMessage> data = Flux.just(WebsocketMessageUtils.fromObject(objToSend));
        for (WebSocketSession session : getAllSessions()) {
            session.send(data).subscribe();
        }
    }
}
