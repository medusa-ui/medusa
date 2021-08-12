package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.util.SessionToHash;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

public class EventHandlerRegistry {

    private static final EventHandlerRegistry INSTANCE = new EventHandlerRegistry();

    public static EventHandlerRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, UIEventController> registry = new HashMap<>();
    private final Map<String, String> hashRegistry = new HashMap<>();

    public void add(String reqPath, Object bean) {
        registry.put(reqPath, new UIEventController(bean));
        hashRegistry.put(Integer.toString(reqPath.hashCode()), reqPath);
    }

    public UIEventController get(String reqPath) {
        return registry.get(reqPath);
    }

    public UIEventController findByHash(String hash) { return get(findKeyByHash(hash)); }

    public UIEventController get(WebSocketSession session) {
        String htmlFileName = SessionToHash.parse(session);
        return findByHash(htmlFileName);
    }

    public String findKeyByHash(String hash) {
        return hashRegistry.get(hash);
    }
}
