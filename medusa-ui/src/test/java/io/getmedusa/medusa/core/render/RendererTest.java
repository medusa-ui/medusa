package io.getmedusa.medusa.core.render;

import io.getmedusa.medusa.core.boot.hydra.HydraConnectionController;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import io.getmedusa.medusa.core.tags.MedusaDialect;
import io.getmedusa.medusa.core.tags.action.MedusaOnClick;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

//we test the actual tags in their own setup, this is purely the rendering setup
class RendererTest {

    //as such, this does not need to be exhaustive, just representative
    private static final Set<AbstractProcessorDialect> DIALECTS = Set.of(new MedusaDialect(List.of(new MedusaOnClick())));

    @Mock
    private HydraConnectionController hydraConnectionController;

    private Renderer rendererWithoutHydra;

    private Renderer rendererWithHydra;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(hydraConnectionController.askHydraForFragment(Mockito.anyMap(), Mockito.anyMap())).thenReturn(Mono.just(List.of()));
        rendererWithoutHydra = new Renderer(DIALECTS, null);
        rendererWithHydra = new Renderer(DIALECTS, hydraConnectionController);
    }

    @Test
    void testRenderSimple() {
        final Flux<DataBuffer> dataBufferFlux = rendererWithoutHydra.render("<p>Hello world</p>", new Session());
        String render = FluxUtils.dataBufferFluxToString(dataBufferFlux);
        System.out.println(render);
        Assertions.assertTrue(render.contains("<script src=\"/websocket.js\"></script>"), "Plain render should contain script");
        Assertions.assertTrue(render.contains("<p>Hello world</p>"));
    }

    @Test
    void testRenderFragmentSimple() {
        final Flux<DataBuffer> dataBufferFlux = rendererWithoutHydra.renderFragment("<p>Hello world</p>", new HashMap<>());
        String render = FluxUtils.dataBufferFluxToString(dataBufferFlux);
        System.out.println(render);
        Assertions.assertFalse(render.contains("<script src=\"/websocket.js\"></script>"), "Fragment should not contain script");
        Assertions.assertTrue(render.contains("<p>Hello world</p>"));
    }

    @Test
    void testRenderSimpleWHydra() {
        final Flux<DataBuffer> dataBufferFlux = rendererWithHydra.render("<p>Hello world</p>", new Session());
        String render = FluxUtils.dataBufferFluxToString(dataBufferFlux);
        System.out.println(render);
        Assertions.assertTrue(render.contains("<script src=\"/websocket.js\"></script>"), "Plain render should contain script");
        Assertions.assertTrue(render.contains("<p>Hello world</p>"));
    }

    @Test
    void testRenderFragmentSimpleWHydra() {
        final Flux<DataBuffer> dataBufferFlux = rendererWithHydra.renderFragment("<p>Hello world</p>", new HashMap<>());
        String render = FluxUtils.dataBufferFluxToString(dataBufferFlux);
        System.out.println(render);
        Assertions.assertFalse(render.contains("<script src=\"/websocket.js\"></script>"), "Fragment should not contain script");
        Assertions.assertTrue(render.contains("<p>Hello world</p>"));
    }

}
