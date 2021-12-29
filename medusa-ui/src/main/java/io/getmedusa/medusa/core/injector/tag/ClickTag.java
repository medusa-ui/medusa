package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class ClickTag {

    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        Elements mClickItems = result.getDocument().getElementsByAttribute(TagConstants.M_CLICK);

        for(Element mClickItem : mClickItems) {
            mClickItem.attr(TagConstants.M_CLICK_REPLACEMENT, replacement(mClickItem.attr(TagConstants.M_CLICK)));
            mClickItem.removeAttr(TagConstants.M_CLICK);
        }

        return result;
    }

    private String replacement(String attrContent) {
        final String replacedTagContent = attrContent
                .replace("'", "\\'")
                .replace("\"", "\\'");
        return "_M.sendEvent(this, '"+replacedTagContent+"')";
    }
}
