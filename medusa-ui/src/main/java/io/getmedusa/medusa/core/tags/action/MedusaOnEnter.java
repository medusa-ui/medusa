package io.getmedusa.medusa.core.tags.action;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@MedusaTag
public class MedusaOnEnter extends JSEventAttributeProcessor {

    // <input m:onenter="search(:{this})"></input>

    public MedusaOnEnter() {
        super("enter", "onkeyup", EVENT_TEMPLATE_M_DO_ACTION_ONKEYUP);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        attributeValue = replaceAttributeValues(context, attributeValue);
        attributeValue = replaceElementValues(attributeValue);
        structureHandler.setAttribute(eventName, eventTemplate.formatted("'Enter'", attributeValue));
    }
}
