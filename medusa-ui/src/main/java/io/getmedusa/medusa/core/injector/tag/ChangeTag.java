package io.getmedusa.medusa.core.injector.tag;

import org.springframework.stereotype.Component;

@Component
public class ChangeTag extends AbstractTag {

    @Override
    protected String pattern() {
        return standardPattern();
    }

    @Override
    protected String substitutionLogic(String fullMatch, String tagContent) {
        return "oninput=\"_M.sendEvent(this, '"+tagContent+"')\"";
    }

    @Override
    protected String tagValue() {
        return "m-change";
    }
}
