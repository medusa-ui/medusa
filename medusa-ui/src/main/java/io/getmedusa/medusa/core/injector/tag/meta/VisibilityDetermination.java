package io.getmedusa.medusa.core.injector.tag.meta;

import io.getmedusa.medusa.core.injector.tag.TagConstants;
import io.getmedusa.medusa.core.util.ExpressionEval;
import org.jsoup.nodes.Element;

import java.util.Map;

public class VisibilityDetermination {

    private static final VisibilityDetermination INSTANCE = new VisibilityDetermination();
    public static VisibilityDetermination getInstance() {
        return INSTANCE;
    }

    public ConditionResult determine(Map<String, Object> variables, Element conditionalElement) {
        final Object conditionItemValue = getConditionItemValue(variables, conditionalElement);

        boolean isMainElementVisible = false;
        if(conditionalElement.hasAttr(TagConstants.CONDITIONAL_TAG_EQUALS)) {
            Object comparisonItemValue = getComparisonItemValue(variables, conditionalElement);
            isMainElementVisible = conditionItemValue.equals(comparisonItemValue);
        }

        //TODO more comparisons: gte, lte, gt, lt
        return new ConditionResult(isMainElementVisible, "");
    }

    private Object getComparisonItemValue(Map<String, Object> variables, Element conditionalElement) {
        final String comparisonItem = conditionalElement.attr(TagConstants.CONDITIONAL_TAG_EQUALS);
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
