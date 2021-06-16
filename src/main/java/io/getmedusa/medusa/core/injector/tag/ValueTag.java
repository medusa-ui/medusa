package io.getmedusa.medusa.core.injector.tag;

import java.util.UUID;

public class ValueTag extends AbstractTag {

    @Override
    String tagValue() {
        return "$";
    }

    @Override
    String pattern() {
        return "\\[\\$.*\\]";
    }

    @Override
    String substitutionLogic(String fullMatch, String tagContent) {
        return "<span id=\"" + UUID.randomUUID().toString() + "\" from-value=\'" + tagContent + "\'>0</span>";
    }
}
