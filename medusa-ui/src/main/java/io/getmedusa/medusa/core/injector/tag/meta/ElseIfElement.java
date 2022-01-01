package io.getmedusa.medusa.core.injector.tag.meta;

import io.getmedusa.medusa.core.injector.tag.meta.VisibilityDetermination.ConditionResult;
import io.getmedusa.medusa.core.util.WrapperUtils;
import org.jsoup.nodes.Element;

import java.util.Map;

public class ElseIfElement {

    private final Element element;
    private final String condition;
    private final boolean valid;

    public ElseIfElement(Element element, Map<String, Object> variables) {
        final ConditionResult conditionResult = VisibilityDetermination.getInstance().determine(variables, element);
        this.condition = conditionResult.condition();
        this.valid = conditionResult.visible();
        this.element = WrapperUtils.wrapAndReplace(element, "m-if-else");
    }

    public Element getElement() {
        return element;
    }

    public String getCondition() {
        return condition;
    }

    public boolean isValid() {
        return valid;
    }
}
