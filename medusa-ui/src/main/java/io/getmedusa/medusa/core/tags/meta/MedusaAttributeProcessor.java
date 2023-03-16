package io.getmedusa.medusa.core.tags.meta;

import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;

import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.*;

public abstract class MedusaAttributeProcessor extends AbstractAttributeTagProcessor {

    public MedusaAttributeProcessor(String attributeName) {
        super(
                templateMode,
                prefix,
                null,
                false,
                attributeName,
                true,
                precedence - 999,  // process prior to JSEventAttributeProcessor
                true);
    }

}
