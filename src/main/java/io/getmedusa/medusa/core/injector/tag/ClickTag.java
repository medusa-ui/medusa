package io.getmedusa.medusa.core.injector.tag;

import org.springframework.stereotype.Component;

@Component
public class ClickTag extends AbstractTag {

    @Override
    String pattern() {
        return standardPattern();
    }

    @Override
    String substitutionLogic(String fullMatch, String tagContent) {
        tagContent = tagContent.replace("'", "\\'");
        return "onclick=\"sendEvent(this, '"+tagContent+"')\"";
    }

    @Override
    String tagValue() {
        return "m-click";
    }
}
