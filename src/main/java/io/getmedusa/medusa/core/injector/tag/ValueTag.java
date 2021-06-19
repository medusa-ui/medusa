package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;

import java.util.Map;

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
        return "<span from-value=\'" + tagContent + "\'>" + wrapWithMarkers(tagContent) + "</span>";
    }

    public InjectionResult injectWithVariables(InjectionResult result, Map<String, Object> variables) {
        result = inject(result);

        for(Map.Entry<String, Object> variableEntrySet : variables.entrySet()) {
            if(variableEntrySet.getValue() != null) result.replaceAll(wrapWithRegexMarkers(variableEntrySet.getKey()), variableEntrySet.getValue().toString());
        }

        return result;
    }

    private String wrapWithMarkers(String stringToWrap) {
        return "[$"+stringToWrap+"]";
    }

    private String wrapWithRegexMarkers(String stringToWrap) {
        return "\\[\\$\\s*?"+stringToWrap+"\\s*?\\]";
    }
}
