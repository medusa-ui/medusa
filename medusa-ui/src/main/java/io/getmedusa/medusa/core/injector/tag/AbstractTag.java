package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.util.ExpressionEval;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractTag implements Tag {

    protected String variableToString(String variableKey, Map<String, Object> variables) {
        keyValidation(variableKey);

        final Object value = ExpressionEval.evalItemAsString(variableKey, variables);
        if(value == null) return null;
        return value.toString();
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
