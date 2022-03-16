package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.registry.EachValueRegistry;
import io.getmedusa.medusa.core.util.ElementUtils;
import io.getmedusa.medusa.core.util.ExpressionEval;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTag implements Tag {

    protected String variableToString(String variableKey, Map<String, Object> variables) {
        keyValidation(variableKey);

        final Object value = ExpressionEval.evalItemAsString(variableKey, variables);
        if(value == null) return null;
        return value.toString();
    }

    protected Object getPossibleEachValue(Element currentElem, String eachName, ServerRequest request, Map<String, Object> variables) {
        if(ElementUtils.hasTemplateAsParent(currentElem)) return eachName;

        Map<String, Object> variablesCopy = new HashMap<>(variables);

        String nameToSearch = eachName;
        String restOfValue = null;
        boolean requiresObjectIntrospection = nameToSearch.contains(".") || nameToSearch.contains("[");
        if(requiresObjectIntrospection) {
            final String[] split = nameToSearch.split("[.\\[]", 2);
            nameToSearch = split[0];
            restOfValue = split[1];
            if(restOfValue.endsWith("]")) restOfValue = "[" + restOfValue;
        }

        //given variableA[variableB]
        Element parentWithEachName = findParentWithEachName(currentElem.parents(), nameToSearch);
        if(null != parentWithEachName) {
            //the following resolves variableA as an each value
            String indexAsString = parentWithEachName.attr(TagConstants.INDEX);
            if(indexAsString.length() == 0) indexAsString = "0";
            int index = Integer.parseInt(indexAsString);
            Object valueToReturn = EachValueRegistry.getInstance().get(request, nameToSearch, index);

            if(requiresObjectIntrospection) {
                String expression = eachName;
                expression = expression.replace(nameToSearch + "[" + nameToSearch + ".key]", nameToSearch + ".value");
                variablesCopy.put(nameToSearch, valueToReturn);
                valueToReturn = ExpressionEval.evalItemAsObj(expression, variablesCopy);
            }
            return valueToReturn;
        } else if(restOfValue != null) {
            //the following checks if [variableB] could be an each value
            if(restOfValue.startsWith("[") && restOfValue.endsWith("]")) {
                String restOfValueWithoutBrackets = restOfValue.substring(1, restOfValue.length() - 1);
                Object bracketValue = getPossibleEachValue(currentElem, restOfValueWithoutBrackets, request, variables);
                if(null == bracketValue) return null;
                String expression = eachName.replace(restOfValueWithoutBrackets, "'" + bracketValue + "'");
                return ExpressionEval.evalItemAsObj(expression, variablesCopy);
            }
        }

        return null;
    }

    private Element findParentWithEachName(Elements parents, String eachName) {
        for(Element parent : parents) {
            if(parent.hasAttr(TagConstants.M_EACH) && parent.attr(TagConstants.M_EACH).equals(eachName)) return parent;
        }
        return null;
    }

    protected Object parseConditionWithVariables(String condition, Map<String, Object> variables) {
        keyValidation(condition);
        final Object variable = ExpressionEval.evalItemAsObj(condition, variables);
        if(variable == null) return Collections.emptyList();
        return variable;
    }

    private void keyValidation(String variableKey) {
        if(variableKey == null) throw new IllegalStateException("Variable key '" + variableKey + "' should either exist or shows an error in internal parsing logic.");
    }
}
