package io.getmedusa.medusa.core.tags.action;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.regex.Matcher;

@MedusaTag
public class MedusaOnClick extends JSEventAttributeProcessor {

    //<button m:click="increment()">increment</button>

    public MedusaOnClick() {
        super("click", "onclick", "_M.doAction(null, '%s')");
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {

        attributeValue = replaceAttributeValues(context, attributeValue);
        attributeValue = replaceElementValues(context, attributeValue);

        String formatted = attributeValue.replace("'", "\\'");
        structureHandler.setAttribute(eventName, eventTemplate.formatted(formatted));
    }

    private String replaceElementValues(ITemplateContext context, String attributeValue) {
        if(attributeValue.contains("#")) {
            Matcher elementValueMatcher = CTX_ELEMENT_VALUE_REGEX.matcher(attributeValue);
            Document document = Jsoup.parse(context.getTemplateData().getTemplate());
            while (elementValueMatcher.find()) {
                String id = elementValueMatcher.group(1);
                Element element = document.getElementById(id);
                String replaceValue = "";
                if (element.hasAttr("value")) {
                    replaceValue = element.val();
                } else {
                    replaceValue = element.text();
                }
                attributeValue = attributeValue.replace(elementValueMatcher.group(), replaceValue);
            }
        }
        return attributeValue;
    }

    private String replaceAttributeValues(ITemplateContext context, String attributeValue) {
        Matcher attributeMatcher = CTX_ATTRIBUTE_VALUE_REGEX.matcher(attributeValue);
        while (attributeMatcher.find()) {
            String replaceValue = attributeMatcher.group(1);
            attributeValue = attributeValue.replace(attributeMatcher.group(), context.getVariable(replaceValue).toString());
        }
        return attributeValue;
    }
}