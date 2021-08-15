package io.getmedusa.medusa.core.injector.tag;

import org.springframework.stereotype.Component;

@Component
public class ClickTag extends AbstractTag {

    @Override
    protected String pattern() {
        return standardPattern();
    }

    @Override
    protected String substitutionLogic(String fullMatch, String tagContent) {
        final String replacedTagContent = tagContent.replace("'", "\\'");
        return "onclick=\"_M.sendEvent(this, '"+replacedTagContent+"')\"";
    }

    @Override
    protected String tagValue() {
        return "m-click";
    }
}
