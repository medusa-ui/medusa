package io.getmedusa.medusa.core.tags.attribute;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.MedusaAttributeProcessor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

/**
 * Reference to the controller.
 * Referencing the controller is optional and does not do anything, it just is linking the page with the controller.
 * This reference will not be display in the resulting HTML.
 */
@MedusaTag
public class MedusaControllerAttribute extends MedusaAttributeProcessor {

    public static final String ATTRIBUTE_NAME = "controller";

    public MedusaControllerAttribute(){
         super(ATTRIBUTE_NAME);
     }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        // nothing to do just remove m:controller
    }
}
