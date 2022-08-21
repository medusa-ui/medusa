package io.getmedusa.medusa.core.tags.action;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@MedusaTag
public class MedusaOnSubmit extends JSEventAttributeProcessor {

    public MedusaOnSubmit() {
            super("submit", "onsubmit", EVENT_TEMPLATE_M_DO_ACTION.replace("doAction", "doFormAction"));
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler
    structureHandler) {
        attributeValue = replaceAttributeValues(context, tag, attributeValue);
        structureHandler.setAttribute(eventName, eventTemplate.formatted(attributeValue));
    }
}
