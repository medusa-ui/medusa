package io.getmedusa.medusa.core.router.action;

import io.getmedusa.diffengine.Engine;
import io.getmedusa.diffengine.model.AbstractDiff;
import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.RouteDetection;
import io.getmedusa.medusa.core.memory.SessionMemoryRepository;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;

class SocketHandlerTest {

    private SocketHandler socketHandler;

    @Mock
    private SessionMemoryRepository sessionMemoryRepository;

    @Mock
    private ActionHandler actionHandler;

    @Mock
    private Renderer renderer;

    @Mock
    private Engine diffEngine;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        socketHandler = new SocketHandler(sessionMemoryRepository, actionHandler, renderer, false);
    }

    @Test
    void testEventEmitter() {
        SocketAction socketAction = new SocketAction();
        socketAction.setAction("doActionWithoutParams()");
        socketAction.setFragment(null);
        socketAction.setMetadata(new HashMap<>());

        RouteDetection.INSTANCE.consider(new SampleController());

        Session session = new Session();
        session.setLastRenderedHTML("test-a");
        Mockito.when(sessionMemoryRepository.retrieve(anyString(), any())).thenReturn(session);
        Mockito.when(actionHandler.executeAndMerge(any(), any(), any())).thenReturn(Mono.just(session));
        Mockito.when(renderer.render(anyString(), any())).thenReturn(createDataBuffer("test"));
        Mockito.when(diffEngine.calculate(nullable(String.class), nullable(String.class))).thenReturn(Set.of(new ServerSideDiff(AbstractDiff.DiffType.ADDITION)));

        socketHandler.eventEmitter(new HashMap<>(),
                Flux.just(socketAction),
                findSampleHash(),
                "sessionId",
                "en-US");

        //TODO StepVerifier.create(ServerSideDiffFlux).assertNext(Assertions::assertNotNull).then(ServerSideDiffFlux.ter)
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
