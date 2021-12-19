package io.getmedusa.medusa.core.injector.tag;

import org.springframework.stereotype.Component;

@Component
public class ChangeTag extends _AbstractTag {

    @Override
    String pattern() {
        return standardPattern();
    }

    @Override
    String substitutionLogic(String fullMatch, String tagContent) {
        return "oninput=\"_M.sendEvent(this, '"+tagContent+"')\"";
    }

    @Override
    String tagValue() {
        return "m-change";
    }
}
