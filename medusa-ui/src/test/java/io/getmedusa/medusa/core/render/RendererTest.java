package io.getmedusa.medusa.core.render;

import io.getmedusa.medusa.core.boot.hydra.HydraConnectionController;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.tags.MedusaDialect;
import io.getmedusa.medusa.core.tags.action.MedusaOnClick;
import io.getmedusa.medusa.core.util.FluxUtils;
import io.getmedusa.medusa.core.validation.ValidationMessageResolver;
import org.jsoup.Jsoup;
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

import java.util.List;
import java.util.Set;

//we test the actual tags in their own setup, this is purely the rendering setup
class RendererTest {

    //as such, this does not need to be exhaustive, just representative
    private static final Set<AbstractProcessorDialect> DIALECTS = Set.of(new MedusaDialect(List.of(new MedusaOnClick())));

    @Mock
    private HydraConnectionController hydraConnectionController;

    @Mock
    private ValidationMessageResolver resolver;

    private Renderer rendererWithoutHydra;

    private Renderer rendererWithHydra;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(hydraConnectionController.askHydraForFragment(Mockito.any(), Mockito.anyMap(), Mockito.any())).thenReturn(Mono.just(List.of()));
        rendererWithoutHydra = new Renderer(DIALECTS, null, "self", resolver);
        rendererWithHydra = new Renderer(DIALECTS, hydraConnectionController,  "self", resolver);
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
        final Flux<DataBuffer> dataBufferFlux = rendererWithoutHydra.renderFragment("<p>Hello world</p>", new Session());
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
        final Flux<DataBuffer> dataBufferFlux = rendererWithHydra.renderFragment("<p>Hello world</p>", new Session());
        String render = FluxUtils.dataBufferFluxToString(dataBufferFlux);
        System.out.println(render);
        Assertions.assertFalse(render.contains("<script src=\"/websocket.js\"></script>"), "Fragment should not contain script");
        Assertions.assertTrue(render.contains("<p>Hello world</p>"));
    }

    @Test
    void testCDATAWrap() {
        final String htmlWithScript = """
                <!DOCTYPE html>
                <html lang="en">
                 <body>
                  <script src="/webjars/highlightjs/highlight.min.js"></script>
                  <script type="text/javascript">
                    function helloWorld() {}
                   </script>
                   <script type="text/javascript">
                     //<![CDATA[ function helloWorld() {}
                     //]]>
                   </script>
                  <div id="container"></div>
                """;

        final String html = rendererWithoutHydra.wrapScriptContentInCDATA(Jsoup.parse(htmlWithScript)).html();
        System.out.println(html);
        Assertions.assertEquals(2, countOccurences(html, "//<![CDATA["));
        Assertions.assertEquals(2, countOccurences(html, "//]]>"));
    }

    private int countOccurences(String str, String findStr) {
        int lastIndex = 0;
        int count = 0;

        while(lastIndex != -1){

            lastIndex = str.indexOf(findStr,lastIndex);

            if(lastIndex != -1){
                count ++;
                lastIndex += findStr.length();
            }
        }

        return count;
    }

}
