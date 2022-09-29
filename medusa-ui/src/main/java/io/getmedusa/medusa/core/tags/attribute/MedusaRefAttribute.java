package io.getmedusa.medusa.core.tags.attribute;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.MedusaAttributeProcessor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@MedusaTag
public class MedusaRefAttribute extends MedusaAttributeProcessor {

    public static final String ATTRIBUTE_NAME = "ref";

    public MedusaRefAttribute(){
         super(ATTRIBUTE_NAME);
     }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
       // nothing to do just remove m:ref
    }
}
