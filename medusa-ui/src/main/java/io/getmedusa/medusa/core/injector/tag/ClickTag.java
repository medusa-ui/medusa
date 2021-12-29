package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class ClickTag implements StandardReplacementTag {

    @Override
    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        replaceAttribute(result.getDocument().getElementsByAttribute(TagConstants.M_CLICK),
                TagConstants.M_CLICK,
                TagConstants.M_CLICK_REPLACEMENT);
        return result;
    }

    @Override
    public String replacement(String attrContent) {
        return "_M.sendEvent(this, '"+attrContent+"')";
    }
}
