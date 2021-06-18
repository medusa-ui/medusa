package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.injector.tag.ChangeTag;
import io.getmedusa.medusa.core.injector.tag.ClickTag;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.injector.tag.ValueTag;
import io.getmedusa.medusa.core.registry.PageTitleRegistry;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum HTMLInjector {

    INSTANCE;

    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final String EVENT_EMITTER = "/event-emitter/";
    private String script = null;

    private final ClickTag clickTag;
    private final ChangeTag changeTag;
    private final ValueTag valueTag;

    HTMLInjector() {
        this.clickTag = new ClickTag();
        this.changeTag = new ChangeTag();
        this.valueTag = new ValueTag();
    }

    public String inject(Resource html, Resource scripts) {
        try {
            if(script == null) script = StreamUtils.copyToString(scripts.getInputStream(), CHARSET);
            String htmlString = StreamUtils.copyToString(html.getInputStream(), CHARSET);
            PageTitleRegistry.getInstance().addTitle(html, htmlString);
            return htmlStringInject(html.getFilename(), htmlString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected String htmlStringInject(String filename, String htmlString) {
        InjectionResult result = clickTag.inject(htmlString);
        result = changeTag.inject(result.getHtml());
        result = valueTag.inject(result);
        return injectScript(filename, result);
    }

    private String injectScript(String filename, InjectionResult html) {
        if(script != null) {
            return html.replaceFinal("</body>",
                    "<script>\n" +
                    script.replaceFirst("%WEBSOCKET_URL%", "ws://localhost:8080" + EVENT_EMITTER + filename.substring(0, filename.length()-5)) +
                    "</script>\n</body>");
        }
        return html.getHtml();
    }
}
