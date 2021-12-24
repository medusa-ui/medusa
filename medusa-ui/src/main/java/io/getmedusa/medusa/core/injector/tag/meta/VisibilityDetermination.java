package io.getmedusa.medusa.core.injector.tag.meta;

import io.getmedusa.medusa.core.injector.tag.TagConstants;
import io.getmedusa.medusa.core.util.ExpressionEval;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.util.Map;

public class VisibilityDetermination {

    private static final VisibilityDetermination INSTANCE = new VisibilityDetermination();
    public static VisibilityDetermination getInstance() {
        return INSTANCE;
    }

    public ConditionResult determine(Map<String, Object> variables, Element conditionalElement) {
        final Object conditionItemValue = getConditionItemValue(variables, conditionalElement);

        Boolean visible = null;
        if(conditionalElement.hasAttr(TagConstants.CONDITIONAL_TAG_EQUALS)) {
            Object comparisonItemValue = getComparisonItemValue(variables, conditionalElement, TagConstants.CONDITIONAL_TAG_EQUALS);
            visible = logicalAnd(visible, conditionItemValue.equals(comparisonItemValue));
        }

        if(conditionalElement.hasAttr(TagConstants.CONDITIONAL_TAG_NOT)) {
            Object comparisonItemValue = getComparisonItemValue(variables, conditionalElement, TagConstants.CONDITIONAL_TAG_NOT);
            visible = logicalAnd(visible, !conditionItemValue.equals(comparisonItemValue));
        }

        if(conditionalElement.hasAttr(TagConstants.CONDITIONAL_TAG_GREATER_THAN)) {
            Object comparisonItemValue = getComparisonItemValue(variables, conditionalElement, TagConstants.CONDITIONAL_TAG_GREATER_THAN);
            visible = logicalAnd(visible, doBigDecimalComparison(conditionItemValue, comparisonItemValue) > 0);
        }

        if(conditionalElement.hasAttr(TagConstants.CONDITIONAL_TAG_LESS_THAN)) {
            Object comparisonItemValue = getComparisonItemValue(variables, conditionalElement, TagConstants.CONDITIONAL_TAG_LESS_THAN);
            visible = logicalAnd(visible, doBigDecimalComparison(conditionItemValue, comparisonItemValue) < 0);
        }

        if(conditionalElement.hasAttr(TagConstants.CONDITIONAL_TAG_GREATER_THAN_OR_EQ)) {
            Object comparisonItemValue = getComparisonItemValue(variables, conditionalElement, TagConstants.CONDITIONAL_TAG_GREATER_THAN_OR_EQ);
            final int comparison = doBigDecimalComparison(conditionItemValue, comparisonItemValue);
            visible = logicalAnd(visible, comparison > 0 || comparison == 0);
        }

        if(conditionalElement.hasAttr(TagConstants.CONDITIONAL_TAG_LESS_THAN_OR_EQ)) {
            Object comparisonItemValue = getComparisonItemValue(variables, conditionalElement, TagConstants.CONDITIONAL_TAG_LESS_THAN_OR_EQ);
            final int comparison = doBigDecimalComparison(conditionItemValue, comparisonItemValue);
            visible = logicalAnd(visible, comparison < 0 || comparison == 0);
        }

        return new ConditionResult(Boolean.TRUE.equals(visible), "");
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
        final String conditionItem = conditionalElement.attr(TagConstants.CONDITIONAL_TAG_CONDITION_ATTR);
        Object conditionItemValue = ExpressionEval.evalItemAsObj(conditionItem, variables);
        if(null == conditionItemValue) conditionItemValue = conditionItem;
        return conditionItemValue;
    }

    private VisibilityDetermination() {}

    public class ConditionResult {
        private final boolean visible;
        private final String condition;

        public ConditionResult(boolean visible, String condition) {
            this.visible = visible;
            this.condition = condition;
        }

        public boolean isVisible() {
            return visible;
        }

        public String getCondition() {
            return condition;
        }
    }
}
