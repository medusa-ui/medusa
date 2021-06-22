package io.getmedusa.medusa.core.injector.tag;

import org.springframework.stereotype.Component;

@Component
public class LinkTag extends AbstractTag {

    @Override
    String pattern() {
        return tagValue() + "=(\"|').+?(\"|')";
    }

    @Override
    String substitutionLogic(String fullMatch, String tagContent) {
        return "href=\"javascript:changePage('"+tagContent+"')\"";
    }

    @Override
    String tagValue() {
        return "m-href";
    }
}
