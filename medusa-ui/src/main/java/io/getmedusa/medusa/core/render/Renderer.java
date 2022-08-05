package io.getmedusa.medusa.core.render;

import io.getmedusa.medusa.core.boot.Fragment;
import io.getmedusa.medusa.core.boot.FragmentDetection;
import io.getmedusa.medusa.core.boot.RefDetection;
import io.getmedusa.medusa.core.boot.StaticResourcesDetection;
import io.getmedusa.medusa.core.boot.hydra.HydraConnectionController;
import io.getmedusa.medusa.core.boot.hydra.model.meta.RenderedFragment;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.TEXT_HTML;

/**
 * Thymeleaf renderer
 */
@Component
public class Renderer {

    public static final String CDATA_START = "//<![CDATA[ ";
    public static final String CDATA_END = "//]]>";
    private static Pattern scriptPattern = Pattern.compile("<script[^>]*>(.+?)</script>", Pattern.DOTALL | Pattern.MULTILINE);

    private final SpringWebFluxTemplateEngine engine;
    private final DataBufferFactory bufferFactory;
    final IEngineConfiguration configuration;
    final HydraConnectionController hydraConnectionController;

    //see https://www.baeldung.com/spring-thymeleaf-fragments#markup-selector
    protected static final Set<String> MARKUP_SELECTORS = new HashSet<>();
    protected static final HashMap<String, Object> TEMPLATE_RESOLUTION_ATTRIBUTES = new HashMap<>();
    protected static final Locale LOCALE = Locale.getDefault();

    private final String selfName;

    /**
     * Thymeleaf renderer
     * @param dialects a set of additional custom Thymeleaf-dialects
     */
    public Renderer(Set<AbstractProcessorDialect> dialects,
                    @Autowired(required = false) HydraConnectionController hydraConnectionController,
                    @Value("${medusa.name:this}") String selfName) {
        this.bufferFactory = new DefaultDataBufferFactory();

        SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        dialects.forEach(templateEngine::addDialect);
        this.engine = templateEngine;
        this.configuration = engine.getConfiguration();
        this.hydraConnectionController = hydraConnectionController;

        this.selfName = selfName;
    }

    public Flux<DataBuffer> render(String templateHTML, Session session) {
        return loadFragments(templateHTML, session).flatMapMany(html -> {
            //TODO try making this a SpringWebFluxEngineContext?
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

        final List<Fragment> localFragmentsToRender = fragmentsToLoad.getOrDefault(selfName, new ArrayList<>());
        localFragmentsToRender.addAll(fragmentsToLoad.getOrDefault("self", new ArrayList<>()));

        mono = renderLocalFragments(localFragmentsToRender, session);

        fragmentsToLoad.remove(selfName);
        fragmentsToLoad.remove("self");

        if(hydraConnectionController == null || hydraConnectionController.isInactive()) {
            mono = buildFallbackFlux(mono, fragmentsToLoad);
        } else {
            mono = hydraConnectionController.askHydraForFragment(mono, fragmentsToLoad, session.toLastParameterMap());
        }
        return mono.map(renderedFragments -> applyRenderedFragmentsToHTML(templateHTML, fragmentsToLoad, renderedFragments));
    }

    private Mono<List<RenderedFragment>> renderLocalFragments(List<Fragment> localFragmentsToRender, Session session) {
        Flux<RenderedFragment> flux = Flux.empty();
        for (Fragment localFragment : localFragmentsToRender) {
            flux = flux.concatWith(renderLocalFragment(localFragment, session));
        }
        return flux.collectList();
    }

    private Flux<RenderedFragment> renderLocalFragment(Fragment fragment, Session session) {
        String rawHTML = RefDetection.INSTANCE.findRef(fragment.getRef());
        if(rawHTML == null) { rawHTML = fragment.getFallback(); }
        return renderFragment(rawHTML, session.toLastParameterMap()).map(dataBuffer -> {
            final RenderedFragment renderedFragment = new RenderedFragment();
            renderedFragment.setId(fragment.getId());
            renderedFragment.setRenderedHTML(FluxUtils.dataBufferToString(dataBuffer));
            return renderedFragment;
        });
    }

    private Mono<List<RenderedFragment>> buildFallbackFlux(Mono<List<RenderedFragment>> selfLoadedFragments, Map<String, List<Fragment>> fragmentsToLoad) {
        List<RenderedFragment> fallbackFragments = new ArrayList<>();
        for(List<Fragment> fragmentList : fragmentsToLoad.values()) {
            for(Fragment fragment : fragmentList) {
                RenderedFragment fallbackFragment = new RenderedFragment();
                fallbackFragment.setId(fragment.getId());
                fallbackFragment.setRenderedHTML(null);
                fallbackFragments.add(fallbackFragment);
            }
        }
        return selfLoadedFragments.map(fragments -> {
            fragments.addAll(fallbackFragments);
            return fragments;
        });
    }

    private String applyRenderedFragmentsToHTML(final String templateHTML, final Map<String, List<Fragment>> fragmentsToLoad, final List<RenderedFragment> renderedFragments) {
        if(renderedFragments == null) {
            return templateHTML;
        }
        String html = templateHTML;
        for(RenderedFragment renderedFragment : renderedFragments) {
            String renderedHtml = renderedFragment.getRenderedHTML();

            if(renderedHtml == null) {
                Fragment relevantFragment = findRelevantFragment(renderedFragment.getId(), fragmentsToLoad);
                if(relevantFragment == null) {
                    return templateHTML;
                }
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
        String html = wrapScriptContentInCDATA(source);
        final Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }

    private String wrapScriptContentInCDATA(String html) {
        Matcher matcher = scriptPattern.matcher(html);
        while(matcher.find()) {
            String script = matcher.group(1);
            html = html.replace(script, CDATA_START  + script + CDATA_END );
        }
        return html;
    }

    private String appendRSocketScriptAndAddHydraPath(String rawTemplate, Session session) {
        String wsURL = "null";
        if(hydraConnectionController != null && !hydraConnectionController.isInactive()) {
            final String wsUrlReplacement = hydraConnectionController.getWSUrl(session.getHydraPath());
            if(wsUrlReplacement != null) { wsURL = "\"" + wsUrlReplacement + "\""; }
        }
        return StaticResourcesDetection.INSTANCE.prependStaticUrlsWithHydraPath(rawTemplate
                .replace("</body>", "\t<script src=\"/websocket.js\"></script>\n" +
                        "<script>_M.controller = '" + session.getLastUsedHash() + "'; " +
                        "_M.sessionId = '" + session.getId() + "'; " +
                        "_M.wsURL = " + wsURL + ";" +
                        "</script>\n" + "</body>"), session);
    }

    public Flux<DataBuffer> renderFragment(String html, Map<String, Object> attributes) {
        IContext context = new EngineContext(configuration, null, TEMPLATE_RESOLUTION_ATTRIBUTES, LOCALE, attributes);
        return Flux.from(engine.processStream(convertToXHTML(html), MARKUP_SELECTORS, context, bufferFactory, TEXT_HTML, UTF_8));
    }
}
