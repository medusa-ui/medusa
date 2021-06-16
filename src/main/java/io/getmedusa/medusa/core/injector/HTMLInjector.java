package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.injector.tag.ClickTag;
import io.getmedusa.medusa.core.injector.tag.InjectionResult;
import io.getmedusa.medusa.core.injector.tag.ValueTag;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum HTMLInjector {

    INSTANCE;

    public static final Charset CHARSET = StandardCharsets.UTF_8;
    private String script = null;

    private final ClickTag clickTag;
    private final ValueTag valueTag;

    HTMLInjector() {
        this.clickTag = new ClickTag();
        this.valueTag = new ValueTag();
    }

    public String inject(Resource html, Resource scripts) {
        try {
            if(script == null) script = StreamUtils.copyToString(scripts.getInputStream(), CHARSET);
            String htmlString = StreamUtils.copyToString(html.getInputStream(), CHARSET);
            return htmlStringInject(htmlString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String htmlStringInject(String htmlString) {
        InjectionResult result = clickTag.inject(htmlString);
        result = valueTag.inject(result);
        return injectScript(result);
    }

    private String injectScript(InjectionResult html) {
        return html.replaceFinal("</body>", "<script>\n" + script + "</script>\n</body>");
    }
}
