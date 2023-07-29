package io.getmedusa.medusa.core.tags.action;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@MedusaTag
public class MedusaValidate extends JSEventAttributeProcessor {

    public MedusaValidate() {
            super("validation", "", "");
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler
    structureHandler) {
        attributeValue = replaceAttributeValues(context, attributeValue);
        attributeValue = replaceElementValues(attributeValue);
        IAttribute existingClass = tag.getAttribute("class");
        if("all".equals(attributeValue)) {
            //- replace m:validation="all" tag with class="error" validation="form-global", careful if it already has a class
            structureHandler.setAttribute("validation", "form-global");
            if(existingClass == null) {
                structureHandler.setAttribute("class", "error");
            } else {
                structureHandler.setAttribute("class", "error " + existingClass.getValue());
            }
        } else {
            //- replace m:validation="value" tag with class="error hidden" validation="value"
            structureHandler.setAttribute("validation", attributeValue);
            if(existingClass == null) {
                structureHandler.setAttribute("class", "error hidden");
            } else {
                structureHandler.setAttribute("class", "error hidden " + existingClass.getValue());
            }
        }
    }
}
