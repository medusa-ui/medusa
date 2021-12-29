package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

public class ClassAppendTag extends AbstractTag {

    @Override
    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        final Elements classAppendAttributes = result.getDocument().getElementsByAttribute(TagConstants.M_CLASS_APPEND);
        for(Element classAppendAttribute : classAppendAttributes) {
            final String newClass = classAppendAttribute.attr(TagConstants.M_CLASS_APPEND);

            Object variableValue = getPossibleEachValue(classAppendAttribute, newClass, request);
            if(null == variableValue) variableValue = variableToString(newClass, variables);
            if(null == variableValue) variableValue = newClass;

            classAppendAttribute.removeAttr(TagConstants.M_CLASS_APPEND);
            classAppendAttribute.addClass(variableValue.toString());
        }
        return result;
    }


}

