package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.cache.HTMLCache;
import io.getmedusa.medusa.core.injector.tag.*;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    private final ClickTag clickTag;
    private final OnEnterTag onEnterTag;
    private final ChangeTag changeTag;
    private final ValueTag valueTag;
    private final ConditionalTag conditionalTag;
    private final IterationTag iterationTag;
    private final ClassAppendTag classAppendTag;
    private final GenericMTag genericMTag;

    HTMLInjector() {
        this.clickTag = new ClickTag();
        this.onEnterTag = new OnEnterTag();
        this.changeTag = new ChangeTag();
        this.valueTag = new ValueTag();
        this.conditionalTag = new ConditionalTag();
        this.iterationTag = new IterationTag();
        this.classAppendTag = new ClassAppendTag();
        this.genericMTag = new GenericMTag();
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
    public String inject(ServerRequest request, SecurityContext securityContext, String fileName, String script, String styling) {
        try {
            if(this.script == null) this.script = script;
            if(this.styling == null) this.styling = styling;
            String htmlString = HTMLCache.getInstance().getHTML(fileName);
            return htmlStringInject(request, securityContext, htmlString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    String htmlStringInject(ServerRequest request, SecurityContext securityContext, String htmlString) {
        final Map<String, Object> variables = newLargestFirstMap();

        final String matchedPath = matchRequestPath(request);
        final UIEventController uiEventController = EventHandlerRegistry.getInstance().get(matchedPath);
        if( null != uiEventController ) variables.putAll(uiEventController.setupAttributes(request, securityContext).getPageVariables());
        InjectionResult result = iterationTag.injectWithVariables(new InjectionResult(htmlString), variables);
        result = conditionalTag.injectWithVariables(result, variables);
        result = clickTag.inject(result.getHtml());
        result = onEnterTag.inject(result.getHtml());
        result = changeTag.inject(result.getHtml());
        result = valueTag.injectWithVariables(result, variables);
        result = classAppendTag.injectWithVariables(result, variables);
        result = genericMTag.injectWithVariables(result, variables);
        injectVariablesInScript(result, variables);

        return injectScript(matchedPath, result);
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
            String variablesAsScript = "_M.variables = " + MAPPER.writeValueAsString(variables) + ";";
            result.addScript(variablesAsScript);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String injectScript(String path, InjectionResult html) {
        String injectedHTML = html.getHtml();
        if(script != null) {
            injectedHTML = html.replaceFinal("</body>",
                    "<script id=\"m-websocket-setup\">\n" +
                    script.replaceFirst("%WEBSOCKET_URL%", EVENT_EMITTER + path.hashCode()) +
                    "</script>\n<script id=\"m-variable-setup\"></script>\n</body>");
        }
        injectedHTML = addStyling(injectedHTML);

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
