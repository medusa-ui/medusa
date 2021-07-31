package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.annotation.UIEventController;
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
        return registry.get(htmlFileName);
    }

    public Object get(WebSocketSession session) {
        String htmlFileName = SessionToHTMLFileName.parse(session);
        return get(htmlFileName);
    }
}
