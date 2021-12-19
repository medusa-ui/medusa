package io.getmedusa.medusa.core.injector.tag;

import org.springframework.stereotype.Component;

@Component
public class ClickTag extends _AbstractTag {

    @Override
    String pattern() {
        return standardPattern();
    }

    @Override
    String substitutionLogic(String fullMatch, String tagContent) {
        final String replacedTagContent = tagContent.replace("'", "\\'");
        return "onclick=\"_M.sendEvent(this, '"+replacedTagContent+"')\"";
    }

    @Override
    String tagValue() {
        return "m-click";
    }
}
