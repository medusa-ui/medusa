package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.RouteDetection;
import io.getmedusa.medusa.core.diffengine.DiffEngine;
import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

class SocketHandlerTest {

    private SocketHandler socketHandler;

    @Mock
    private SessionMemoryRepository sessionMemoryRepository;

    @Mock
    private ActionHandler actionHandler;

    @Mock
    private Renderer renderer;

    @Mock
    private DiffEngine diffEngine;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        socketHandler = new SocketHandler(sessionMemoryRepository, actionHandler, renderer, diffEngine);
    }

    @Test
    void testEventEmitter() {
        SocketAction socketAction = new SocketAction();
        socketAction.setAction("doActionWithoutParams()");
        socketAction.setFragment(null);
        socketAction.setMetadata(new HashMap<>());

        RouteDetection.INSTANCE.consider(new SampleController());

        Mockito.when(actionHandler.executeAndMerge(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new Session());
        Mockito.when(renderer.render(Mockito.anyString(), Mockito.any())).thenReturn(createDataBuffer("test"));
        Mockito.when(diffEngine.findDiffs(Mockito.nullable(String.class), Mockito.nullable(String.class))).thenReturn(Flux.just(List.of(new JSReadyDiff())));

        final Flux<List<JSReadyDiff>> jsReadyDiffFlux = socketHandler.eventEmitter(new HashMap<>(),
                Flux.just(socketAction),
                findSampleHash(),
                "sessionId");

        List<JSReadyDiff> diff = jsReadyDiffFlux.blockLast();
        Assertions.assertNotNull(diff);
    }

    private Flux<DataBuffer> createDataBuffer(String value) {
        DataBufferFactory factory = new DefaultDataBufferFactory();
        return Flux.just(factory.wrap(value.getBytes(StandardCharsets.UTF_8)));
    }

    private String findSampleHash() {
        return RouteDetection.INSTANCE.getDetectedRoutes().stream().findFirst().get().generateHash();
    }

    @UIEventPage(path = "/", file = "/pages/hello-world")
    public class SampleController {

        public List<Attribute> doActionWithoutParams() {
            return List.of(new Attribute("counterValue", 1));
        }

    }

}
