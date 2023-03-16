package io.getmedusa.medusa.core.tags.attribute;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.MedusaAttributeProcessor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@MedusaTag
public class MedusaLoadingAttribute extends MedusaAttributeProcessor {

    public static final String LOADING_UNTIL = "loading-until";
    public static final String DATA_LOADING_UNTIL = "data-loading-until";
    public static final String LOADING_STYLE = "loading-style";
    public static final String DATA_LOADING_STYLE = "data-loading-style";

    public MedusaLoadingAttribute() {
        super(LOADING_UNTIL);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        structureHandler.setAttribute(DATA_LOADING_UNTIL, attributeValue);
        if(tag.hasAttribute(MedusaTag.prefix, LOADING_STYLE)) {
            structureHandler.setAttribute(DATA_LOADING_STYLE, tag.getAttributeValue(MedusaTag.prefix, LOADING_STYLE));
            structureHandler.removeAttribute(MedusaTag.prefix, LOADING_STYLE);
        }
    }
}
