package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.GenericMAttribute;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

public class GenericMTag extends AbstractTag {

    @Override
    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {

        for(GenericMAttribute genericMAttribute : GenericMAttribute.values()) {
            final String attribute = "m:" + genericMAttribute.name().toLowerCase();
            Elements elementsWithAttribute = result.getDocument().getElementsByAttribute(attribute);
            for(Element elementWithAttribute : elementsWithAttribute) {
                final String attributeValue = elementWithAttribute.attr(attribute);
                Object variableValue = getPossibleEachValue(elementWithAttribute, attributeValue, request);
                if(null == variableValue) variableValue = variableToString(attributeValue, variables);
                if(null == variableValue) variableValue = attributeValue;

                final Map<String, String> attributesMap = genericMAttribute.determineValue(variableValue);
                elementWithAttribute.removeAttr(attribute);
                for(Map.Entry<String, String> entrySet : attributesMap.entrySet()) {
                    elementWithAttribute.attr(entrySet.getKey(), entrySet.getValue());
                }
            }
        }

        return result;
    }
}

