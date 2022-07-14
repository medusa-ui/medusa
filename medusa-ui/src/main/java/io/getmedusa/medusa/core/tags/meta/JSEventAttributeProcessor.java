package io.getmedusa.medusa.core.tags.meta;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.regex.Pattern;

import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.*;

public abstract class JSEventAttributeProcessor extends AbstractAttributeTagProcessor {
    protected static final Pattern CTX_VALUE_REGEX = Pattern.compile("\\$\\{(.*)\\}");
    protected final String eventName;
    protected final String eventTemplate;

    protected JSEventAttributeProcessor(String attributeName, String eventName, String eventTemplate) {
        super(
                templateMode,   // This processor will apply only to this template mode (ie HTML)
                prefix,         // Prefix to be applied to name for matching
                null,           // No tag name: match any tag name
                false,          // No prefix to be applied to tag name
                attributeName,  // Name of the attribute that will be matched
                true,           // Apply dialect prefix to attribute name
                precedence,     // Precedence (inside dialect's precedence)
                true);          // Remove the matched attribute afterwards
        this.eventName = eventName;
        this.eventTemplate = eventTemplate;
    }

    @Override
    protected abstract void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler);

}