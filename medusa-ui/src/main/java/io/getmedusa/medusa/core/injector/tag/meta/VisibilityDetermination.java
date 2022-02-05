package io.getmedusa.medusa.core.injector.tag.meta;

import io.getmedusa.medusa.core.injector.tag.AbstractTag;
import io.getmedusa.medusa.core.util.ExpressionEval;
import org.jsoup.nodes.Element;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.tag.TagConstants.*;

public class VisibilityDetermination extends AbstractTag {

    private static final VisibilityDetermination INSTANCE = new VisibilityDetermination();
    public static VisibilityDetermination getInstance() {
        return INSTANCE;
    }

    public ConditionResult determine(Map<String, Object> vars, Element element, ServerRequest request) {
        final Object conditionItem = getConditionItemValue(vars, element, request);
        String item = element.attr(CONDITIONAL_TAG_CONDITION_ATTR);
        StringBuilder condition = new StringBuilder("$#$");
        condition.append(item);
        condition.append("$#$");
        Object value= vars.get(item);

        Boolean visible = null;
        if(element.hasAttr(CONDITIONAL_TAG_EQUALS)) {
            Object comparisonItem = getComparisonItemValue(vars, element, CONDITIONAL_TAG_EQUALS);
            visible = logicalAnd(visible, compareObjects(conditionItem, comparisonItem));
            addComparisonToCondition(condition, comparisonItem, " === ");
        }

        if(element.hasAttr(CONDITIONAL_TAG_NOT)) {
            Object comparisonItem = getComparisonItemValue(vars, element, CONDITIONAL_TAG_NOT);
            visible = logicalAnd(visible, !compareObjects(conditionItem, comparisonItem));
            addComparisonToCondition(condition, comparisonItem, " !== ");
        }

        if(element.hasAttr(CONDITIONAL_TAG_GREATER_THAN)) {
            Object comparisonItem = getComparisonItemValue(vars, element, CONDITIONAL_TAG_GREATER_THAN);
            visible = logicalAnd(visible, doBigDecimalComparison(conditionItem, comparisonItem) > 0);
            addComparisonToCondition(condition, comparisonItem, " > ");
        }

        if(element.hasAttr(CONDITIONAL_TAG_LESS_THAN)) {
            Object comparisonItem = getComparisonItemValue(vars, element, CONDITIONAL_TAG_LESS_THAN);
            visible = logicalAnd(visible, doBigDecimalComparison(conditionItem, comparisonItem) < 0);
            addComparisonToCondition(condition, comparisonItem, " < ");
        }

        if(element.hasAttr(CONDITIONAL_TAG_GREATER_THAN_OR_EQ)) {
            Object comparisonItem = getComparisonItemValue(vars, element, CONDITIONAL_TAG_GREATER_THAN_OR_EQ);
            final int comparison = doBigDecimalComparison(conditionItem, comparisonItem);
            visible = logicalAnd(visible, comparison > 0 || comparison == 0);
            addComparisonToCondition(condition, comparisonItem, ">=");
        }

        if(element.hasAttr(CONDITIONAL_TAG_LESS_THAN_OR_EQ)) {
            Object comparisonItem = getComparisonItemValue(vars, element, CONDITIONAL_TAG_LESS_THAN_OR_EQ);
            final int comparison = doBigDecimalComparison(conditionItem, comparisonItem);
            visible = logicalAnd(visible, comparison < 0 || comparison == 0);
            addComparisonToCondition(condition, comparisonItem, "<=");
        }

        if(element.hasAttr(CONDITIONAL_TAG_EMPTY)) {
            Object comparisonItem = getComparisonItemValue(vars, element, CONDITIONAL_TAG_EMPTY);

            String comparisonToken = "===";
            String combinationToken = "||";
            if (Boolean.FALSE.toString().equals(comparisonItem)) {
                comparisonToken = "!==";
                combinationToken = "&&";
                visible = logicalAnd(visible, !isEmpty(value));
            } else {
                visible = logicalAnd(visible, isEmpty(value));
            }
            addConditionNullCheck(condition, comparisonToken, combinationToken);
        }

        return new ConditionResult(Boolean.TRUE.equals(visible), condition.toString());
    }

    private void addConditionNullCheck(StringBuilder condition, String comparisonToken, String combinationToken) {
        String conditionToString = condition.toString();
        condition.append(" ");
        condition.append(comparisonToken);
        condition.append(" null ");
        condition.append(combinationToken);
        condition.append(" typeof ");
        condition.append(conditionToString);
        condition.append(" ");
        condition.append(comparisonToken);
        condition.append(" \"undefined\" ");
        condition.append(combinationToken);
        condition.append(" Object.keys(");
        condition.append(conditionToString);
        condition.append(").length ");
        condition.append(comparisonToken);
        condition.append(" 0 ");
        condition.append(combinationToken);
        condition.append(" ");
        condition.append(conditionToString);
        condition.append(" ");
        condition.append(comparisonToken);
        condition.append(" ''");
    }

    private boolean isEmpty(Object value) {
        boolean empty = value == null;
        if (value instanceof String string) {
            empty = string.isBlank();
        } else if (value instanceof Collection<?> collection) {
            empty = collection.isEmpty();
        } else if ( value instanceof Map<?, ?> map){
            empty = map.isEmpty();
        }
        return empty;
    }

    private void addComparisonToCondition(StringBuilder condition, Object comparisonItem, String comparisonToken) {
        condition.append(comparisonToken);
        condition.append(comparisonItem);
    }

    private boolean compareObjects(Object conditionItem, Object comparisonItem) {
        return wrapped(conditionItem).equals(wrapped(comparisonItem)) ||
                asBoolean(conditionItem).equals(asBoolean(comparisonItem));
    }

    private Object asBoolean(Object item) {
        if(Boolean.TRUE.toString().equals(item)) return true;
        return item;
    }

    private String wrapped(Object item) {
        if(null == item) return "''";
        if(!item.toString().startsWith("'")) {
            return "'" + item + "'";
        }
        return item.toString();
    }

    private Boolean logicalAnd(Boolean existingValue, boolean newValue) {
        if(existingValue == null) return newValue;
        return Boolean.logicalAnd(existingValue, newValue);
    }

    private int doBigDecimalComparison(Object conditionItemValue, Object comparisonItemValue) {
        try {
            BigDecimal conditionItemValueAsBigDecimal = new BigDecimal(conditionItemValue.toString());
            BigDecimal comparisonItemValueAsBigDecimal = new BigDecimal(comparisonItemValue.toString());
            return conditionItemValueAsBigDecimal.compareTo(comparisonItemValueAsBigDecimal);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Object getComparisonItemValue(Map<String, Object> variables, Element conditionalElement, String tag) {
        final String comparisonItem = conditionalElement.attr(tag);
        Object comparisonItemValue = ExpressionEval.evalItemAsObj(comparisonItem, variables);
        if(null == comparisonItemValue) comparisonItemValue = comparisonItem;
        return comparisonItemValue;
    }

    private Object getConditionItemValue(Map<String, Object> variables, Element conditionalElement, ServerRequest request) {
        final String conditionItem = conditionalElement.attr(CONDITIONAL_TAG_CONDITION_ATTR);
        try {
            Object conditionItemValue = ExpressionEval.evalItemAsObj(conditionItem, variables);
            if(null == conditionItemValue) conditionItemValue = conditionItem;
            Object variableValue = getPossibleEachValue(conditionalElement, conditionItem, request, variables);
            if(null != variableValue) conditionItemValue = variableValue;
            return conditionItemValue;
        } catch (SpelEvaluationException e) {
            Object variableValue = getPossibleEachValue(conditionalElement, conditionItem, request, variables);
            if(null != variableValue) return variableValue;
            return conditionItem;
        }
    }

    private VisibilityDetermination() {}

    @Override
    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        return null;
    }

    public record ConditionResult(boolean visible, String condition) {

    }
}
