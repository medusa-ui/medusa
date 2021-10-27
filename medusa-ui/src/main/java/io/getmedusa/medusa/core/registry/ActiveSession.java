package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.socket.WebSocketSession;

public class ActiveSession {

    private final WebSocketSession webSocketSession;
    private SecurityContext securityContext;
    private String uniqueId;

    public ActiveSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
