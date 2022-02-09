package io.getmedusa.medusa.core.injector.tag.meta;

import io.getmedusa.medusa.core.injector.tag.meta.VisibilityDetermination.ConditionResult;
import io.getmedusa.medusa.core.util.WrapperUtils;
import org.jsoup.nodes.Element;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

public class ElseIfElement {

    private final Element element;
    private final String condition;
    private final boolean valid;

    public ElseIfElement(Element element, Map<String, Object> variables, ServerRequest request, String ifCondition) {
        final ConditionResult conditionResult = VisibilityDetermination.getInstance().determine(variables, element, request);
        this.condition =
                new StringBuilder("!( ")
                        .append(ifCondition)
                        .append(" ) && (")
                        .append(conditionResult.condition())
                        .append(")")
                        .toString();
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
