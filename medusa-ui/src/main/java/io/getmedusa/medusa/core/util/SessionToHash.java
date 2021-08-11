package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.HTMLInjector;
import org.springframework.web.reactive.socket.WebSocketSession;

public class SessionToHash {

    private SessionToHash() {}

    public static String parse(WebSocketSession session) {
        String uri = session.getHandshakeInfo().getUri().toString();
        int indexOfEventEmitter = uri.indexOf(HTMLInjector.EVENT_EMITTER);
        if(-1 == indexOfEventEmitter) return null;
        return uri.substring(indexOfEventEmitter + HTMLInjector.EVENT_EMITTER.length());
    }

}
