package io.getmedusa.medusa.core.injector.tag.meta;

import io.getmedusa.medusa.core.util.ExpressionEval;
import org.jsoup.nodes.Element;
import org.springframework.expression.spel.SpelEvaluationException;

import java.math.BigDecimal;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.tag.TagConstants.*;

public class VisibilityDetermination {

    private static final VisibilityDetermination INSTANCE = new VisibilityDetermination();
    public static VisibilityDetermination getInstance() {
        return INSTANCE;
    }

    public ConditionResult determine(Map<String, Object> vars, Element element) {
        final Object conditionItem = getConditionItemValue(vars, element);
        StringBuilder condition = new StringBuilder(element.attr(CONDITIONAL_TAG_CONDITION_ATTR));

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

        return new ConditionResult(Boolean.TRUE.equals(visible), condition.toString());
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
        if(!item.toString().startsWith("'")) return "'" + item + "'";
        return item.toString();
    }

    private Boolean logicalAnd(Boolean existingValue, boolean newValue) {
        if(existingValue == null) return newValue;
        return Boolean.logicalAnd(existingValue, newValue);
    }

    private int doBigDecimalComparison(Object conditionItemValue, Object comparisonItemValue) {
        BigDecimal conditionItemValueAsBigDecimal = new BigDecimal(conditionItemValue.toString());
        BigDecimal comparisonItemValueAsBigDecimal = new BigDecimal(comparisonItemValue.toString());
        return conditionItemValueAsBigDecimal.compareTo(comparisonItemValueAsBigDecimal);
    }

    private Object getComparisonItemValue(Map<String, Object> variables, Element conditionalElement, String tag) {
        final String comparisonItem = conditionalElement.attr(tag);
        Object comparisonItemValue = ExpressionEval.evalItemAsObj(comparisonItem, variables);
        if(null == comparisonItemValue) comparisonItemValue = comparisonItem;
        return comparisonItemValue;
    }

    private Object getConditionItemValue(Map<String, Object> variables, Element conditionalElement) {
        final String conditionItem = conditionalElement.attr(CONDITIONAL_TAG_CONDITION_ATTR);
        try {
            Object conditionItemValue = ExpressionEval.evalItemAsObj(conditionItem, variables);
            if(null == conditionItemValue) conditionItemValue = conditionItem;
            return conditionItemValue;
        } catch (SpelEvaluationException e) {
            return conditionItem;
        }
    }

    private VisibilityDetermination() {}

    public record ConditionResult(boolean visible, String condition) {

    }
}
