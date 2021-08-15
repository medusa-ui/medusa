package io.getmedusa.medusa.core.injector.tag;

import org.springframework.stereotype.Component;

@Component
public class OnEnterTag extends AbstractTag {

    @Override
    protected String pattern() {
        return standardPattern();
    }

    @Override
    protected String substitutionLogic(String fullMatch, String tagContent) {
        final String replacedTagContent = tagContent.replace("'", "\\'");
        return "onkeydown=\"_M.preventDefault(event)\" onkeyup=\"_M.onEnter(this, '"+replacedTagContent+"', event)\"";
    }

    @Override
    protected String tagValue() {
        return "m-onenter";
    }
}
