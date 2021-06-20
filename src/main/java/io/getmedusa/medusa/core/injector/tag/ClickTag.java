package io.getmedusa.medusa.core.injector.tag;

import org.springframework.stereotype.Component;

@Component
public class ClickTag extends AbstractTag {

    @Override
    String pattern() {
        return tagValue() + "=(\"|').+?(\"|')";
    }

    @Override
    String substitutionLogic(String fullMatch, String tagContent) {
        return "onclick=\"sendEvent('"+tagContent+"')\"";
    }

    @Override
    String tagValue() {
        return "m-click";
    }
}
