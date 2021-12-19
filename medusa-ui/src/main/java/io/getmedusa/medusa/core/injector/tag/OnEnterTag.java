package io.getmedusa.medusa.core.injector.tag;

import org.springframework.stereotype.Component;

@Component
public class OnEnterTag extends _AbstractTag {

    @Override
    String pattern() {
        return standardPattern();
    }

    @Override
    String substitutionLogic(String fullMatch, String tagContent) {
        final String replacedTagContent = tagContent.replace("'", "\\'");
        return "onkeydown=\"_M.preventDefault(event)\" onkeyup=\"_M.onEnter(this, '"+replacedTagContent+"', event)\"";
    }

    @Override
    String tagValue() {
        return "m-onenter";
    }
}
