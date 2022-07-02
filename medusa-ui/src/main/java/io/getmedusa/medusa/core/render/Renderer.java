package io.getmedusa.medusa.core.render;

import io.getmedusa.medusa.core.boot.Fragment;
import io.getmedusa.medusa.core.boot.FragmentDetection;
import io.getmedusa.medusa.core.boot.StaticResourcesDetection;
import io.getmedusa.medusa.core.boot.hydra.HydraConnectionController;
import io.getmedusa.medusa.core.boot.hydra.model.meta.RenderedFragment;
import io.getmedusa.medusa.core.session.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.TEXT_HTML;

/**
 * Thymeleaf renderer
 */
@Component
public class Renderer {

    private final SpringWebFluxTemplateEngine engine;
    private final DataBufferFactory bufferFactory;
    final IEngineConfiguration configuration;
    final HydraConnectionController hydraConnectionController;

    //see https://www.baeldung.com/spring-thymeleaf-fragments#markup-selector
    protected static final Set<String> MARKUP_SELECTORS = new HashSet<>();
    protected static final HashMap<String, Object> TEMPLATE_RESOLUTION_ATTRIBUTES = new HashMap<>();
    protected static final Locale LOCALE = Locale.getDefault();

    /**
     * Thymeleaf renderer
     * @param dialects a set of additional custom Thymeleaf-dialects
     */
    public Renderer(Set<AbstractProcessorDialect> dialects, @Autowired(required = false) HydraConnectionController hydraConnectionController) {
        this.bufferFactory = new DefaultDataBufferFactory();

        SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        dialects.forEach(templateEngine::addDialect);
        this.engine = templateEngine;
        this.configuration = engine.getConfiguration();
        this.hydraConnectionController = hydraConnectionController;
    }

    public Flux<DataBuffer> render(String templateHTML, Session session) {
        return loadFragments(templateHTML, session).flatMapMany(html -> {
            IContext context = new EngineContext(configuration, null, TEMPLATE_RESOLUTION_ATTRIBUTES, LOCALE, session.toLastParameterMap());
            return Flux.from(engine.processStream(
                    appendRSocketScriptAndAddHydraPath(convertToXHTML(html), session),
                    MARKUP_SELECTORS, context, bufferFactory, TEXT_HTML, UTF_8)
            );
        });
    }

    private Mono<String> loadFragments(final String templateHTML, Session session) {
        final Map<String, List<Fragment>> fragmentsToLoad = FragmentDetection.INSTANCE.detectWhichFragmentsArePresent(templateHTML);
        Mono<List<RenderedFragment>> mono;

        if(hydraConnectionController == null || hydraConnectionController.isInactive()) {
            mono = buildFallbackFlux(fragmentsToLoad);
        } else {
            mono = hydraConnectionController.askHydraForFragment(fragmentsToLoad, session.toLastParameterMap());
        }
        return mono.map(renderedFragments -> applyRenderedFragmentsToHTML(templateHTML, fragmentsToLoad, renderedFragments));
    }

    private Mono<List<RenderedFragment>> buildFallbackFlux(Map<String, List<Fragment>> fragmentsToLoad) {
        List<RenderedFragment> fallbackFragments = new ArrayList<>();
        for(List<Fragment> fragmentList : fragmentsToLoad.values()) {
            for(Fragment fragment : fragmentList) {
                RenderedFragment fallbackFragment = new RenderedFragment();
                fallbackFragment.setId(fragment.getId());
                fallbackFragment.setRenderedHTML(null);
                fallbackFragments.add(fallbackFragment);
            }
        }
        return Mono.just(fallbackFragments);
    }

    private String applyRenderedFragmentsToHTML(final String templateHTML, final Map<String, List<Fragment>> fragmentsToLoad, final List<RenderedFragment> renderedFragments) {
        if(renderedFragments == null) return templateHTML;
        String html = templateHTML;
        for(RenderedFragment renderedFragment : renderedFragments) {
            String renderedHtml = renderedFragment.getRenderedHTML();

            if(renderedHtml == null) {
                Fragment relevantFragment = findRelevantFragment(renderedFragment.getId(), fragmentsToLoad);
                if(relevantFragment == null) return templateHTML;
                renderedHtml = relevantFragment.getFallback();
            }

            html = html.replace(renderedFragment.getId(), renderedHtml);
        }
        return html;
    }

    private Fragment findRelevantFragment(String id, Map<String, List<Fragment>> fragmentsToLoad) {
        for(List<Fragment> fragmentList : fragmentsToLoad.values()) {
            for(Fragment fragment : fragmentList) {
                if(fragment.getId().equals(id)) {
                    return fragment;
                }
            }
        }
        return null;
    }

    private String convertToXHTML(String source) {
        final Document document = Jsoup.parse(source);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }

    public String appendRSocketScriptAndAddHydraPath(String rawTemplate, Session session) {
        final String hydraPathToAdd = (session.getHydraPath() != null) ? "'" + session.getHydraPath() + "'" : "null";
        return StaticResourcesDetection.INSTANCE.prependStatisUrlsWithHydraPath(rawTemplate
                .replace("</body>", "\t<script src=\"/websocket.js\"></script>\n" +
                        "<script>_M.controller = '" + session.getLastUsedHash() + "'; " +
                        "_M.sessionId = '" + session.getId() + "'; " +
                        "_M.hydraPath = " + hydraPathToAdd + ";" +
                        "</script>\n" + "</body>"), session);
    }

    public Flux<DataBuffer> renderFragment(String html, Map<String, Object> attributes) {
        IContext context = new EngineContext(configuration, null, TEMPLATE_RESOLUTION_ATTRIBUTES, LOCALE, attributes);
        return Flux.from(engine.processStream(convertToXHTML(html), MARKUP_SELECTORS, context, bufferFactory, TEXT_HTML, UTF_8));
    }
}
