package io.getmedusa.medusa.core.render;

import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.Fragment;
import io.getmedusa.medusa.core.boot.FragmentDetection;
import io.getmedusa.medusa.core.boot.RefDetection;
import io.getmedusa.medusa.core.boot.StaticResourcesDetection;
import io.getmedusa.medusa.core.boot.hydra.HydraConnectionController;
import io.getmedusa.medusa.core.boot.hydra.model.meta.RenderedFragment;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import io.getmedusa.medusa.core.util.FragmentUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.TEXT_HTML;

/**
 * Thymeleaf renderer
 */
@Component
public class Renderer {

    public static final String CDATA_START = "//<![CDATA[ ";
    public static final String CDATA_END = "//]]>";
    protected static final String TOP_LOADER = "<div id=\"m-top-load-bar\" class=\"progress-line\" style=\"display:none;\"></div>\n";
    protected static final String FULL_LOADER = "<div id=\"m-full-loader\" style=\"display:none;\">Loading ...</div>";
    protected static final String LOADING_STYLE = """
            <style>
                    .loading {
                        display: inline-block;
                        position: relative;
                        width: 1em;
                        height: 1em;
                        -webkit-font-smoothing: antialiased;
                        -moz-osx-font-smoothing: grayscale;
                        opacity: 0.6;
                    }
                    .loading span {
                        box-sizing: border-box;
                        display: block;
                        position: absolute;
                        width: 1em;
                        height: 1em;
                        border: 0.15em solid;
                        border-radius: 50%;
                        animation: loading-spin 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
                        border-color: gray transparent transparent transparent;
                    }
                    .loading span:nth-child(1) {
                        animation-delay: -0.45s;
                    }
                    .loading span:nth-child(2) {
                        animation-delay: -0.3s;
                    }
                    .loading span:nth-child(3) {
                        animation-delay: -0.15s;
                    }
                    @keyframes loading-spin {
                        0% {
                            transform: rotate(0deg);
                        }
                        100% {
                            transform: rotate(360deg);
                        }
                    }
                    div#m-top-load-bar {
                        position: fixed;
                        top: 0;
                        left: 0;
                        right: 0;
                        width: 100%;
                    }
                    .progress-line, .progress-line:before {
                        height: 3px;
                        width: 100%;
                        margin: 0;
                    }
                    .progress-line {
                        background-color: #7a00ff;
                        display: -webkit-flex;
                        display: flex;
                    }
                    .progress-line:before {
                        background-color: #f4abba;
                        content: '';
                        -webkit-animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
                        animation: running-progress 2s cubic-bezier(0.4, 0, 0.2, 1) infinite;
                    }
                    @-webkit-keyframes running-progress {
                        0% { margin-left: 0px; margin-right: 100%; }
                        50% { margin-left: 25%; margin-right: 0%; }
                        100% { margin-left: 100%; margin-right: 0; }
                    }
                    @keyframes running-progress {
                        0% { margin-left: 0px; margin-right: 100%; }
                        50% { margin-left: 25%; margin-right: 0%; }
                        100% { margin-left: 100%; margin-right: 0; }
                    }
                                            
                    div#m-full-loader {
                        background: #0000006e;
                        position: fixed;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        text-align: center;
                        padding-top: 15%;
                    }
                </style></body>
            """;
    protected static final String END_OF_BODY = "</body>";

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
                    @Value("${medusa.name:self}") String selfName) {
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
        final Map<String, List<Fragment>> fragmentsToLoad = FragmentDetection.INSTANCE.detectWhichFragmentsArePresent(templateHTML, session);
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
        final String ref = fragment.getRef();
        String rawHTML = RefDetection.INSTANCE.findRef(ref);

        if(rawHTML == null) {
            rawHTML = fragment.getFallback();
        } else {
            UIEventPageCallWrapper bean = RefDetection.INSTANCE.findBeanByRef(ref);
            if(session.isInitialRender()) { //TODO ? what happens 'on action'?
                //call controller startup and use for render (add to session? separate?)
                List<Attribute> attributes = bean.setupAttributes(null, session);
                session = session.merge(attributes);
            }
        }

        //TODO deal with the export/imports here as well later on

        return renderFragment(rawHTML, session).map(dataBuffer -> {
            final RenderedFragment renderedFragment = new RenderedFragment();
            renderedFragment.setId(fragment.getId());
            renderedFragment.setRenderedHTML(FragmentUtils.addFragmentRefToHTML(FluxUtils.dataBufferToString(dataBuffer), ref));
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

    private String convertToXHTML(String html) {
        final Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        final Document wrappedDoc = wrapScriptContentInCDATA(document);
        boolean isFragment = FragmentUtils.determineIfFragment(wrappedDoc);
        String outerHtml = document.outerHtml();
        if(isFragment) {
            outerHtml = document.body().html();
        }
        return outerHtml;
    }

    Document wrapScriptContentInCDATA(Document document) {
        for(Element scriptElement : document.getElementsByTag("script")) {
            if(!scriptElement.html().isEmpty() && !scriptElement.html().contains(CDATA_START)) {
                scriptElement.prepend(CDATA_START).append(CDATA_END);
            }
        }
        return document;
    }

    private String appendRSocketScriptAndAddHydraPath(String rawTemplate, Session session) {
        String wsURL = "'/socket'";
        if(hydraConnectionController != null && !hydraConnectionController.isInactive() && null != session.getHydraPath()) {
            wsURL = "'/" + session.getHydraPath() + "/socket'";
        }
        return StaticResourcesDetection.INSTANCE.prependStaticUrlsWithHydraPath(rawTemplate
                .replace(END_OF_BODY, TOP_LOADER + FULL_LOADER + LOADING_STYLE)
                .replace(END_OF_BODY, "\t<script src=\"/websocket.js\"></script>\n" +
                        "<script>_M.controller = '" + session.getLastUsedHash() + "'; " +
                        "_M.sessionId = '" + session.getId() + "'; " +
                        "_M.wsURL = " + wsURL + ";" +
                        "_M.wsP = '" + session.getPassword() + "';" +
                        "</script>\n" + END_OF_BODY), session);
    }

    @Deprecated
    public Flux<DataBuffer> renderFragment(String html, Map<String, Object> attributes) {
        IContext context = new EngineContext(configuration, null, TEMPLATE_RESOLUTION_ATTRIBUTES, LOCALE, attributes);
        return Flux.from(engine.processStream(convertToXHTML(html), MARKUP_SELECTORS, context, bufferFactory, TEXT_HTML, UTF_8));
    }

    public Flux<DataBuffer> renderFragment(String html, Session session) {
        session.setDepth(session.getDepth() + 1);
        if(session.getDepth() > 100) {
            return Flux.just(FluxUtils.stringToDataBuffer(html));
        }
        return loadFragments(html, session).flatMapMany(parsedHTML -> {
            IContext context = new EngineContext(configuration, null, TEMPLATE_RESOLUTION_ATTRIBUTES, LOCALE, session.toLastParameterMap());
            return Flux.from(engine.processStream(convertToXHTML(parsedHTML), MARKUP_SELECTORS, context, bufferFactory, TEXT_HTML, UTF_8));
        });
    }
}
