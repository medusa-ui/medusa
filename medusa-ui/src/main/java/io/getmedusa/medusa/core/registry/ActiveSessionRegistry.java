package io.getmedusa.medusa.core.registry;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.getmedusa.medusa.core.util.SecurityContext;
import io.getmedusa.medusa.core.util.WebsocketMessageUtils;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActiveSessionRegistry {

    private static final ActiveSessionRegistry INSTANCE = new ActiveSessionRegistry();

    private ActiveSessionRegistry() {
    }

    public static ActiveSessionRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, WebSocketSession> registry = new HashMap<>();
    private final Map<String, SecurityContext> registrySecurityContext = new HashMap<>();

    private static final Cache<String, SecurityContext> securityContextCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    public void add(WebSocketSession session) {
        if (null == session) return;
        System.out.println("Start session: " + session.getId());
        registry.put(session.getId(), session);
    }

    public void remove(WebSocketSession session) {
        if (null == session) return;
        System.out.println("END of session : " + session.getId());
        registry.remove(session.getId());
        registrySecurityContext.remove(session.getId());
    }

    private Collection<WebSocketSession> getAllSessions() {
        return registry.values();
    }

    public void sendToAll(Object objToSend) {
        Flux<WebSocketMessage> data = objToFlux(objToSend);
        for (WebSocketSession session : getAllSessions()) {
            session.send(data).subscribe();
        }
    }

    private Flux<WebSocketMessage> objToFlux(Object objToSend) {
        return Flux.just(WebsocketMessageUtils.fromObject(objToSend));
    }

    public void sendToSession(Object objToSend, String sessionId) {
        Flux<WebSocketMessage> data = objToFlux(objToSend);
        final WebSocketSession webSocketSession = registry.get(sessionId);
        if (null != webSocketSession) webSocketSession.send(data).subscribe();
    }

    public WebSocketSession getWebsocketByID(String id) {
        return registry.get(id);
    }

    public SecurityContext getSecurityContextById(String id) {
        return registrySecurityContext.get(id);
    }

    public void associateSecurityContext(String uniqueSecurityId, WebSocketSession session) {
        registrySecurityContext.put(session.getId(), securityContextCache.getIfPresent(uniqueSecurityId));
    }

    public void registerSecurityContext(SecurityContext securityContext) {
        securityContextCache.put(securityContext.getUniqueId(), securityContext);
    }

    public void clear() {
        registry.clear();
        registrySecurityContext.clear();
        securityContextCache.cleanUp();
    }
}
