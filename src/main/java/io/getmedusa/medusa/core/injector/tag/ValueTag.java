package io.getmedusa.medusa.core.injector.tag;

public class ValueTag extends AbstractTag {

    @Override
    String tagValue() {
        return "$";
    }

    @Override
    String pattern() {
        return "\\[\\$.+?\\]";
    }

    @Override
    String substitutionLogic(String fullMatch, String tagContent) {
        return "<span from-value=\'" + tagContent + "\'>0</span>";
    }
}
