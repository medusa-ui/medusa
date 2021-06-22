package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.cache.HTMLCache;
import io.getmedusa.medusa.core.injector.tag.*;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.util.FilenameHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.getmedusa.medusa.core.websocket.ReactiveWebSocketHandler.MAPPER;

public enum HTMLInjector {

    INSTANCE;

    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final String EVENT_EMITTER = "/event-emitter/";
    private String script = null;

    private final ClickTag clickTag;
    private final ChangeTag changeTag;
    private final ValueTag valueTag;
    private final ConditionalTag conditionalTag;
    private final LinkTag linkTag;

    HTMLInjector() {
        this.clickTag = new ClickTag();
        this.changeTag = new ChangeTag();
        this.valueTag = new ValueTag();
        this.conditionalTag = new ConditionalTag();
        this.linkTag = new LinkTag();
    }

    /**
     * On page load
     * @param getPath
     * @param fileName
     * @param script
     * @return
     */
    public String inject(String getPath, String fileName, String script) {
        try {
            if(this.script == null) this.script = script;
            String htmlString = HTMLCache.getInstance().getHTML(fileName);
            return htmlStringInject(getPath, fileName, htmlString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected String htmlStringInject(String getPath, String filename, String htmlString) {
        final Map<String, Object> variables = RouteRegistry.getInstance().getVariables(getPath);

        InjectionResult result = clickTag.inject(htmlString);
        result = conditionalTag.injectWithVariables(result, variables);
        result = changeTag.inject(result.getHtml());
        result = linkTag.inject(result.getHtml());
        result = valueTag.injectWithVariables(result, variables);
        result = removeTagsFromTitle(result);
        injectVariablesInScript(result, variables);

        return injectScript(filename, result);
    }

    private void injectVariablesInScript(InjectionResult result, Map<String, Object> variables) {
        try {
            String variablesAsScript = "let variables = " + MAPPER.writeValueAsString(variables) + ";";
            result.addScript(variablesAsScript);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String injectScript(String filename, InjectionResult html) {
        if(script != null) {
            return html.replaceFinal("</body>",
                    "<script id=\"websocket-setup\">\n" +
                    script.replaceFirst("%WEBSOCKET_URL%", EVENT_EMITTER + FilenameHandler.removeExtension(filename)) +
                    "</script>\n</body>");
        }
        return html.getHtml();
    }

    protected InjectionResult removeTagsFromTitle(InjectionResult html) {
        return html.removeFromTitle("<span.+?from-value=.+?>").removeFromTitle("<\\/span>");
    }
}
