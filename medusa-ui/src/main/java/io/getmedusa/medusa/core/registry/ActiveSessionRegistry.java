package io.getmedusa.medusa.core.registry;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import io.getmedusa.medusa.core.util.WebsocketMessageUtils;
import io.getmedusa.medusa.core.websocket.DomChangesExecution;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActiveSessionRegistry {

    private static final ActiveSessionRegistry INSTANCE = new ActiveSessionRegistry();
    private static final DomChangesExecution DOM_CHANGES_EXECUTION = new DomChangesExecution();

    private ActiveSessionRegistry() {
    }

    public static ActiveSessionRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, WebSocketSession> registry = new HashMap<>();
    private final Map<String, SecurityContext> registrySecurityContext = new HashMap<>();
    private final Map<String, ActiveDocument> documentRegistry = new HashMap<>();

    private static final Cache<String, SecurityContext> securityContextCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();
    private static final Cache<String, ActiveDocument> activeDocumentCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    public void add(WebSocketSession session) {
        if (null == session) return;
        registry.put(session.getId(), session);
    }

    public void remove(WebSocketSession session) {
        if (null == session) return;
        registry.remove(session.getId());
        registrySecurityContext.remove(session.getId());
        documentRegistry.remove(session.getId());
    }

    private Collection<WebSocketSession> getAllSessions() {
        return registry.values();
    }

    public void sendToAll(Object objToSend) {
        for (WebSocketSession session : getAllSessions()) {
            sendToSession(objToSend, session.getId());
        }
    }

    private Flux<WebSocketMessage> objToFlux(Object objToSend) {
        return Flux.just(WebsocketMessageUtils.fromObject(objToSend));
    }

    public void sendToSession(Object objToSend, String sessionId) {
        final WebSocketSession webSocketSession = registry.get(sessionId);
        final Flux<WebSocketMessage> data = objToFlux(applyDOMExecutionIfApplicable(objToSend, webSocketSession));
        if (null != webSocketSession) webSocketSession.send(data).subscribe();
    }

    private Object applyDOMExecutionIfApplicable(Object objToSend, WebSocketSession webSocketSession) {
        if(objToSend instanceof List<?> list) {
            if(!list.isEmpty() && list.get(0) instanceof DOMChanges.DOMChange) {
                objToSend = DOM_CHANGES_EXECUTION.process(webSocketSession, (List<DOMChanges.DOMChange>) list);
            }
        } else if(objToSend instanceof DOMChanges domChanges) {
            objToSend = DOM_CHANGES_EXECUTION.process(webSocketSession, domChanges.build());
        }
        return objToSend;
    }

    public WebSocketSession getWebsocketByID(String id) {
        return registry.get(id);
    }

    public SecurityContext getSecurityContextById(String id) {
        return registrySecurityContext.get(id);
    }

    public void associateSecurityContext(String uniqueSecurityId, WebSocketSession session) {
        registrySecurityContext.put(session.getId(), securityContextCache.getIfPresent(uniqueSecurityId));
        documentRegistry.put(session.getId(), activeDocumentCache.getIfPresent(uniqueSecurityId));
    }

    public void registerSecurityContext(SecurityContext securityContext) {
        securityContextCache.put(securityContext.getUniqueId(), securityContext);
    }

    public void clear() {
        registry.clear();
        registrySecurityContext.clear();
        documentRegistry.clear();
        activeDocumentCache.cleanUp();
        securityContextCache.cleanUp();
    }

    public void registerDocument(String uniqueSecurityId, ActiveDocument document) {
        activeDocumentCache.put(uniqueSecurityId, document);
    }

    public ActiveDocument getLastDocument(String sessionId) {
        return documentRegistry.getOrDefault(sessionId, ActiveDocument.empty());
    }
}
