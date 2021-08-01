package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.annotation.MEventPageHandler;
import io.getmedusa.medusa.core.util.SessionToHTMLFileName;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

public class EventHandlerRegistry {

    private static final EventHandlerRegistry INSTANCE = new EventHandlerRegistry();

    public static EventHandlerRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, Object> registry = new HashMap<>();

    public void add(String htmlFileName, Object controller) {
        registry.put(htmlFileName, controller);
    }

    public Object get(String htmlFileName) {
        Object controller = registry.get(htmlFileName);
        if(controller instanceof MEventPageHandler) {
            /* no session yet */
            controller = ((MEventPageHandler) controller).instance();
        }
        return controller;
    }

    public Object get(WebSocketSession session) {
        String htmlFileName = SessionToHTMLFileName.parse(session);
        Object controller = registry.get(htmlFileName);
        if(controller instanceof MEventPageHandler) {
            controller = ((MEventPageHandler) controller).bean(session);
        }
        return controller;
    }
}
