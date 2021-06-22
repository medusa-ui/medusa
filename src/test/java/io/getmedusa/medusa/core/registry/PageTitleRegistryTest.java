package io.getmedusa.medusa.core.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.net.URI;

class PageTitleRegistryTest {

    private PageTitleRegistry registry = PageTitleRegistry.getInstance();

    @BeforeEach
    void setup() {
        PageTitleRegistry.getInstance().clear();
    }

    @Test
    void testAddFullTitle() {
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getFilename()).thenReturn("test");
        registry.addTitle("test", "<title>[$counter-value]</title>");
        Assertions.assertEquals("[$counter-value]", getTitleViaRegistry("test"));
    }

    @Test
    void testAddPartialTitle() {
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getFilename()).thenReturn("test");
        registry.addTitle("test", "<title>Hello [$counter-value] Medusa</title>");
        Assertions.assertEquals("Hello [$counter-value] Medusa", getTitleViaRegistry("test"));
    }

    @Test
    void testAddTitleWithHTMLWithNoTitle() {
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getFilename()).thenReturn("test");
        registry.addTitle("test", "<html></html>");
        Assertions.assertNull(getTitleViaRegistry("test"));
    }

    @Test
    void testNoTitleChangeWhenNotEmitter() {
        Assertions.assertNull(getTitleViaRegistry(null));
    }

    @Test
    void testNoTitleChangeWhenNotInRegistry() {
        Assertions.assertNull(getTitleViaRegistry("test"));
    }

    String getTitleViaRegistry(String key) {
        try {
            WebSocketSession webSocketSessionEmptyURI = Mockito.mock(WebSocketSession.class);
            URI uri = new URI("");
            if (key != null) uri = new URI("ws://localhost:8080/event-emitter/" + key);
            HandshakeInfo handshakeEmpty = new HandshakeInfo(uri, new HttpHeaders(), Mono.empty(), null);
            Mockito.when(webSocketSessionEmptyURI.getHandshakeInfo()).thenReturn(handshakeEmpty);
            return registry.getTitle(webSocketSessionEmptyURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
