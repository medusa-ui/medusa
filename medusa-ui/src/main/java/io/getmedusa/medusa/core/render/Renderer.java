package io.getmedusa.medusa.core.render;

import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.boot.*;
import io.getmedusa.medusa.core.boot.hydra.HydraConnectionController;
import io.getmedusa.medusa.core.boot.hydra.model.meta.RenderedFragment;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import io.getmedusa.medusa.core.util.FragmentUtils;
import io.getmedusa.medusa.core.util.LoaderStatics;
import io.getmedusa.medusa.core.validation.ValidationMessageResolver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
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
    protected static final String END_OF_BODY = "</body>";

    private final SpringWebFluxTemplateEngine engine;
    private final DataBufferFactory bufferFactory;
    final IEngineConfiguration configuration;
    final HydraConnectionController hydraConnectionController;

    //see https://www.baeldung.com/spring-thymeleaf-fragments#markup-selector
    protected static final Set<String> MARKUP_SELECTORS = new HashSet<>();
    protected static final HashMap<String, Object> TEMPLATE_RESOLUTION_ATTRIBUTES = new HashMap<>();

    private final String selfName;
    private final ValidationMessageResolver resolver;

    /**
     * Thymeleaf renderer
     * @param dialects a set of additional custom Thymeleaf-dialects
     */
    public Renderer(Set<AbstractProcessorDialect> dialects,
                    @Autowired(required = false) HydraConnectionController hydraConnectionController,
                    @Value("${medusa.name:self}") String selfName,
                    ValidationMessageResolver resolver, MessageSource messageSource) {
        this.bufferFactory = new DefaultDataBufferFactory();

        SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.setMessageSource(messageSource);
        dialects.forEach(templateEngine::addDialect);
        this.engine = templateEngine;
        this.configuration = engine.getConfiguration();
        this.hydraConnectionController = hydraConnectionController;

        this.selfName = selfName;
        this.resolver = resolver;
    }

    public Flux<DataBuffer> render(String templateHTML, Session session) {
        return loadFragments(templateHTML, session).flatMapMany(html -> {
            //TODO try making this a SpringWebFluxEngineContext / SpringWebFluxThymeleafRequestContext?
            IContext context = new EngineContext(configuration, null, TEMPLATE_RESOLUTION_ATTRIBUTES, session.getLocale(), session.toLastParameterMap());
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
            session.addFragmentTag(getFragmentController(localFragment));
            flux = flux.concatWith(renderLocalFragment(localFragment, session));
        }
        return flux.collectList();
    }

    private static String getFragmentController(Fragment localFragment) {
        final UIEventPageCallWrapper beanByRef = RefDetection.INSTANCE.findBeanByRef(localFragment.getRef());
        if(null != beanByRef && null != beanByRef.getController()) {
            return beanByRef.getController().getClass().getName();
        }
        return null;
    }

    private Flux<RenderedFragment> renderLocalFragment(Fragment fragment, Session session) {
        final String ref = fragment.getRef();
        String rawHTML = RefDetection.INSTANCE.findRef(ref);
        final boolean fragmentFallback = rawHTML == null;

        if(fragmentFallback) {
            rawHTML = fragment.getFallback();
        }

        //TODO deal with the export/imports here as well later on

        final String html = rawHTML;

        return session.setupAttributes(ref, fragmentFallback).flatMap(s -> renderFragment(html, s).map(dataBuffer -> {
            final RenderedFragment renderedFragment = new RenderedFragment();
            renderedFragment.setId(fragment.getId());
            renderedFragment.setRenderedHTML(FragmentUtils.addFragmentRefToHTML(FluxUtils.dataBufferToString(dataBuffer), ref));
            return renderedFragment;
        }));
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
        return ModalDetection.INSTANCE.prepFile(outerHtml);
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
                .replace(END_OF_BODY, LoaderStatics.getTopLoader() +
                        LoaderStatics.getFullLoader() +
                        LoaderStatics.getPerButtonLoader() +
                        "<script src=\"/websocket.js\"></script>" +
                        "<script>_M.controller = '" + session.getLastUsedHash() + "'; " +
                        "_M.sessionId = '" + session.getId() + "'; " +
                        "_M.wsURL = " + wsURL + ";" +
                        "_M.wsP = '" + session.getPassword() + "';" +
                        "_M.validationsPossible = " + ValidationDetection.INSTANCE.buildFrontendValidations(session, resolver) + ";" +
                        "</script>\n" + END_OF_BODY), session);
    }

    @Deprecated
    public Flux<DataBuffer> renderFragment(String html, Map<String, Object> attributes, Locale locale) {
        IContext context = new EngineContext(configuration, null, TEMPLATE_RESOLUTION_ATTRIBUTES, locale, attributes);
        return Flux.from(engine.processStream(convertToXHTML(html), MARKUP_SELECTORS, context, bufferFactory, TEXT_HTML, UTF_8));
    }

    public Flux<DataBuffer> renderFragment(String html, Session session) {
        session.setDepth(session.getDepth() + 1);
        if(session.getDepth() > 100) {
            return Flux.just(FluxUtils.stringToDataBuffer(html));
        }
        return loadFragments(html, session).flatMapMany(parsedHTML -> {
            IContext context = new EngineContext(configuration, null, TEMPLATE_RESOLUTION_ATTRIBUTES, session.getLocale(), session.toLastParameterMap());
            return Flux.from(engine.processStream(convertToXHTML(parsedHTML), MARKUP_SELECTORS, context, bufferFactory, TEXT_HTML, UTF_8));
        });
    }
}