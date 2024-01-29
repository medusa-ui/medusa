package io.getmedusa.medusa.core.tags.meta;

import org.thymeleaf.processor.element.AbstractElementTagProcessor;

import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.*;

/**
 * Medusa Tag Processor
 */
public abstract class MedusaElementTagProcessor extends AbstractElementTagProcessor {

    public static final int PRECEDENCE = 1000;

    /**
     * Process tag by elementName
     *
     * @param elementName the element to process
     */
    public MedusaElementTagProcessor(String elementName) {
        super(
                templateMode,
                prefix,
                elementName,
                true,
                null,
                false,
                PRECEDENCE);
    }
}
