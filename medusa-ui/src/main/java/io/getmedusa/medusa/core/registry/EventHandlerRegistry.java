package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.util.SessionToHash;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a singleton instance with all routes and their respective event handler beans - wrapped in a {@link UIEventController}
 * This allows for a quick lookup when a certain route gets called, which event handler should be executed.
 * Gets instantiated and filled up with values during beforeInitialization post-processing, like most registries.
 */
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
