package io.getmedusa.medusa.core.tags.action;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;
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
        attributeValue = replaceAttributeValues(context, tag, attributeValue);
        attributeValue = replaceElementValues(context, tag, attributeValue);
        String formatted = format(attributeValue);
        structureHandler.setAttribute(eventName, eventTemplate.formatted(formatted));
    }
}