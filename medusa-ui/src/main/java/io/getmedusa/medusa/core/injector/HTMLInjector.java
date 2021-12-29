package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.annotation.UIEventWithAttributes;
import io.getmedusa.medusa.core.cache.HTMLCache;
import io.getmedusa.medusa.core.injector.tag.*;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.EachValueRegistry;
import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static io.getmedusa.medusa.core.websocket.ReactiveWebSocketHandler.MAPPER;

/**
 * Handles translation from Medusa expressions to HTML/JS
 */
public enum HTMLInjector {

    INSTANCE;

    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final String EVENT_EMITTER = "/event-emitter/";
    private String script = null;
    private String styling = null;

    private final OnEnterTag onEnterTag;
    private final ClassAppendTag classAppendTag;
    private final GenericMTag genericMTag;

    private final HydraMenuTag hydraMenuTag;
    private final HydraURLReplacer urlReplacer;

    HTMLInjector() {
        this.onEnterTag = new OnEnterTag();
        this.classAppendTag = new ClassAppendTag();
        this.genericMTag = new GenericMTag();
        this.hydraMenuTag = new HydraMenuTag();
        this.urlReplacer = new HydraURLReplacer();
    }

    public static List<Tag> getTags() {
        return List.of(
                new IterationTag(),
                new ValueTag(),
                new ConditionalTag(),
                new ClickTag(),
                new ChangeTag()
        );
    }

    /**
     * On page load, inject cached unparsed HTML with parsed values
     *
     * @param request
     * @param securityContext
     * @param fileName html filename
     * @param script cached websocket script
     * @return parsed html
     */
    public String inject(ServerRequest request, SecurityContext securityContext, String fileName, String script, String styling, String csrfToken) {
        try {
            if(this.script == null) this.script = script;
            if(this.styling == null) this.styling = styling;
            Document document = HTMLCache.getInstance().getDocument(fileName);
            return htmlStringInject(request, securityContext, csrfToken, document);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO
     * @param request
     * @param securityContext
     * @param fileName
     * @param script
     * @param styling
     * @return
     */
    public String inject(ServerRequest request, SecurityContext securityContext, String fileName, String script, String styling) {
        return inject(request, securityContext, fileName, script, styling, null);
    }

    //only used in testing
    String htmlStringInject(ServerRequest request, SecurityContext securityContext, String htmlString) {
        return htmlStringInject(request, securityContext, null, Jsoup.parse(htmlString));
    }

    String htmlStringInject(ServerRequest request, SecurityContext securityContext, String csrfToken, Document document) {
        final Map<String, Object> variables = newLargestFirstMap();

        final String matchedPath = matchRequestPath(request);
        final UIEventWithAttributes uiEventController = EventHandlerRegistry.getInstance().get(matchedPath);
        if(uiEventController != null) variables.putAll(uiEventController.setupAttributes(request, securityContext).getPageVariables());

        try {
            InjectionResult result = new InjectionResult(document);
            List<Tag> tags = HTMLInjector.getTags();
            for(Tag tag : tags) {
                result = tag.inject(result, variables, request);
            }
            injectVariablesInScript(result, variables);

            String html = injectScript(matchedPath, result, securityContext.getUniqueId(), csrfToken);
            return urlReplacer.replaceUrls(html, request.headers());
        } finally {
            EachValueRegistry.getInstance().clear(request);
        }
    }

    private String matchRequestPath(ServerRequest request) {
        if(request.pathVariables().isEmpty()) {
            return request.path();
        } else {
            return findPath(request.path(), RouteRegistry.getInstance().getRoutes(), request.pathVariables());
        }
    }

    public String findPath(String url, Set<String> possiblePaths, Map<String, String> pathVariables) {
        for(String possiblePath : possiblePaths) {
            String path = possiblePath;
            for(Map.Entry<String, String> entry : pathVariables.entrySet()) {
                path = path.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            if(path.equals(url)) return possiblePath;
        }
        return url;
    }

    private void injectVariablesInScript(InjectionResult result, Map<String, Object> variables) {
        try {
            String variablesAsScript =
                    "_M.variables = " + MAPPER.writeValueAsString(variables) + ";" +
                    "_M.conditionals = " + MAPPER.writeValueAsString(IterationRegistry.getInstance().listConditions()) + ";";
            result.addScript(variablesAsScript);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String injectScript(String path, InjectionResult html, String uniqueId, String csrfToken) {
        String injectedHTML = html.getHTML();
        if(script != null) {
            final String bodyEndTagReplacement = "<script id=\"m-websocket-setup\">\n" +
                    script.replaceFirst("%WEBSOCKET_URL%", EVENT_EMITTER + path.hashCode()).replaceFirst("%SECURITY_CONTEXT_ID%", uniqueId) +
                    "</script>\n<script id=\"m-variable-setup\"></script>\n</body>";
            injectedHTML = injectedHTML.replace("</body>", bodyEndTagReplacement);

            //TODO necessary?
            for(String s : html.getScripts()) {
                injectedHTML = injectedHTML.replace("<script id=\"m-variable-setup\"></script>", "<script id=\"m-variable-setup\">" + s + "</script>");
            }
        }
        injectedHTML = addStyling(injectedHTML);
        if(csrfToken != null) injectedHTML = injectedHTML.replace("{{_csrf}}", csrfToken);
        return injectedHTML;
    }

    private String addStyling(String injectedHTML) {
        if(styling != null) {
            injectedHTML = injectedHTML.replace("</head>", styling + "\n</head>");
            if(injectedHTML.contains("m-loading-style=\"top\"")) {
                injectedHTML = injectedHTML.replace("<body>", "<body>\n<div id=\"m-top-load-bar\" class=\"progress-line\" style=\"display:none;\"></div>");
            }
            if(injectedHTML.contains("m-loading-style=\"full\"")) {
                injectedHTML = injectedHTML.replace("<body>", "<body>\n<div id=\"m-full-loader\" style=\"display:none;\">Loading ...</div>");
            }
        }
        return injectedHTML;
    }

    private TreeMap<String, Object> newLargestFirstMap() {
        return new TreeMap<>((s1, s2) -> {
            if(s1.length() < s2.length()) {
                return 1;
            } else if(s1.length() > s2.length()) {
                return -1;
            } else {
                return s1.compareTo(s2);
            }
        });
    }
}
