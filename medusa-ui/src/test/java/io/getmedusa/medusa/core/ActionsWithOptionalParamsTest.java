package io.getmedusa.medusa.core;

import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.registry.ActiveSessionRegistry;
import io.getmedusa.medusa.core.util.ExpressionEval;
import io.getmedusa.medusa.core.util.SecurityContext;
import io.getmedusa.medusa.core.websocket.EventOptionalParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@SpringBootTest
class ActionsWithOptionalParamsTest {

    @Autowired
    private MyController myController;

    private WebSocketSession webSocketSession;

    @BeforeEach
    public void init() {
        webSocketSession = new MyWebSocket(UUID.randomUUID().toString());
        ActiveSessionRegistry.getInstance().add(webSocketSession);
    }

    @Test
    void executeEventWithoutOptionalParameters() {
        String event = EventOptionalParams.rebuildEventWithOptionalParams(MyController.class, webSocketSession, "exampleWithoutSecurityButWithParam(7)");
        DOMChanges result = ExpressionEval.evalEventController(event, myController);

        Assertions.assertNotNull(result, "Expected to get a result back");
        Assertions.assertEquals("exampleWithoutSecurityButWithParam: 7", getVal(result));
    }

    @Test
    void executeEventWithOptionalParameters() {
        String event = EventOptionalParams.rebuildEventWithOptionalParams(MyController.class, webSocketSession, "exampleWithSecurityAndParam(9, 6)");

        DOMChanges result = ExpressionEval.evalEventController(event, myController);

        Assertions.assertNotNull(result, "Expected to get a result back");
        Assertions.assertEquals("exampleWithSecurity and session and param: 9x6y" + webSocketSession.getId(), getVal(result));
    }

    private String getVal(DOMChanges domChanges) {
        return domChanges.build().get(0).getV().toString();
    }

}

@Component
class MyController {

    public DOMChanges exampleWithoutSecurityButWithParam(Integer test) {
        return DOMChanges.of("result", "exampleWithoutSecurityButWithParam: " + test);
    }

    public DOMChanges exampleWithSecurityAndParam(SecurityContext securityContext, Integer test, WebSocketSession session, Integer test2) {
        return DOMChanges.of("result", "exampleWithSecurity and session and param: " + test + "x" + test2 + "y" + session.getId());
    }

    public DOMChanges exampleWithSecurity(SecurityContext securityContext, WebSocketSession session) {
        return DOMChanges.of("result", "exampleWithSecurity and session");
    }

}

class MyWebSocket implements WebSocketSession {

    private String id;

    public MyWebSocket(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public HandshakeInfo getHandshakeInfo() {
        return null;
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Flux<WebSocketMessage> receive() {
        return null;
    }

    @Override
    public Mono<Void> send(Publisher<WebSocketMessage> messages) {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public Mono<Void> close(CloseStatus status) {
        return null;
    }

    @Override
    public Mono<CloseStatus> closeStatus() {
        return null;
    }

    @Override
    public WebSocketMessage textMessage(String payload) {
        return null;
    }

    @Override
    public WebSocketMessage binaryMessage(Function<DataBufferFactory, DataBuffer> payloadFactory) {
        return null;
    }

    @Override
    public WebSocketMessage pingMessage(Function<DataBufferFactory, DataBuffer> payloadFactory) {
        return null;
    }

    @Override
    public WebSocketMessage pongMessage(Function<DataBufferFactory, DataBuffer> payloadFactory) {
        return null;
    }
}