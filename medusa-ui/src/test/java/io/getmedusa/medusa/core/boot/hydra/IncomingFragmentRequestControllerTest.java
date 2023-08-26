package io.getmedusa.medusa.core.boot.hydra;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.Fragment;
import io.getmedusa.medusa.core.boot.RefDetection;
import io.getmedusa.medusa.core.boot.hydra.model.FragmentRequestWrapper;
import io.getmedusa.medusa.core.boot.hydra.model.meta.RenderedFragment;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class IncomingFragmentRequestControllerTest {

    protected static final String PUBLIC = "public";
    protected static final ServerHttpResponse RESPONSE = new MockServerHttpResponse();
    final Renderer renderer = Mockito.mock(Renderer.class);
    final IncomingFragmentRequestController controller = new IncomingFragmentRequestController(PUBLIC, "private", renderer);

    @Captor
    private ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(renderer.renderFragment(Mockito.nullable(String.class), Mockito.any(), Mockito.any(Locale.class)))
                .thenReturn(Flux.just(FluxUtils.stringToDataBuffer("")));

        RefDetection.INSTANCE.addTestRef("ref-sample", new SampleController());
    }

    @UIEventPage(path = "x", file = "z")
    public class SampleController {
        public Mono<List<Attribute>> setupAttributes(){
            return Mono.just(List.of(
                new Attribute("alwaysPresent", true)
            ));
        }
    }

    @Test
    void testNoImpactByDefault() {
        Assertions.fail("TODO");
    }

    @Test
    void testImportIntoFragment() {
        Map<String, Object> attr = new HashMap<>();
        attr.put("aaa", 123);
        attr.put("bbb", 456);

        Fragment f = new Fragment();
        f.setRef("ref-sample");
        f.setImports(List.of("bbb"));

        final FragmentRequestWrapper fragmentRequestWrapper = new FragmentRequestWrapper();
        fragmentRequestWrapper.setAttributes(attr);
        fragmentRequestWrapper.setRequests(List.of(f));

        final Mono<List<RenderedFragment>> mono = controller.requestFragmentRender(fragmentRequestWrapper, PUBLIC, RESPONSE);
        final List<RenderedFragment> renderedFragments = mono.block();

        Assertions.assertNotNull(renderedFragments);
        Assertions.assertEquals(1, renderedFragments.size());
        RenderedFragment fragment = renderedFragments.get(0);
        Assertions.assertNotNull(fragment);

        Mockito.verify(renderer).renderFragment(Mockito.nullable(String.class), mapArgumentCaptor.capture(), Mockito.any(Locale.class));
        final Map<String, Object> attributesForFragmentRender = mapArgumentCaptor.getValue();

        System.out.println(attributesForFragmentRender);
        Assertions.assertTrue(attributesForFragmentRender.containsKey("alwaysPresent"), "Missing alwaysPresent -- This should be an attribute from fragment's setup");
        Assertions.assertTrue(attributesForFragmentRender.containsKey("bbb"), "Missing bbb -- This should be an attribute from origin through import");

        Assertions.assertEquals(2, attributesForFragmentRender.size());
    }

    @Test
    void testImportIntoFragmentWithAlias() {
        Map<String, Object> attr = new HashMap<>();
        attr.put("aaa", 123);
        attr.put("bbb", 456);

        Fragment f = new Fragment();
        f.setRef("ref-sample");
        f.setImports(List.of("bbb as zzz"));

        final FragmentRequestWrapper fragmentRequestWrapper = new FragmentRequestWrapper();
        fragmentRequestWrapper.setAttributes(attr);
        fragmentRequestWrapper.setRequests(List.of(f));

        final Mono<List<RenderedFragment>> mono = controller.requestFragmentRender(fragmentRequestWrapper, PUBLIC, RESPONSE);
        final List<RenderedFragment> renderedFragments = mono.block();

        Assertions.assertNotNull(renderedFragments);
        Assertions.assertEquals(1, renderedFragments.size());
        RenderedFragment fragment = renderedFragments.get(0);
        Assertions.assertNotNull(fragment);

        Mockito.verify(renderer).renderFragment(Mockito.nullable(String.class), mapArgumentCaptor.capture(), Mockito.any(Locale.class));
        final Map<String, Object> attributesForFragmentRender = mapArgumentCaptor.getValue();

        System.out.println(attributesForFragmentRender);
        Assertions.assertTrue(attributesForFragmentRender.containsKey("alwaysPresent"), "Missing alwaysPresent -- This should be an attribute from fragment's setup");
        Assertions.assertTrue(attributesForFragmentRender.containsKey("zzz"), "Missing zzz -- This should be an attribute from origin through import w/ alias");

        Assertions.assertEquals(2, attributesForFragmentRender.size());
    }


}
