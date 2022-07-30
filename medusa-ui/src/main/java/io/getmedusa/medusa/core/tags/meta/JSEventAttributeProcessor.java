package io.getmedusa.medusa.core.tags.meta;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.*;
import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.precedence;

public abstract class JSEventAttributeProcessor extends AbstractAttributeTagProcessor {
    //`search('${document.querySelector("input").value}')`

    public static final String QUERY_SELECTOR_PREFIX = ":"; // avoid collisions with existing Thymeleaf Standard Expression Syntax
    public static final String VARIABLE_PREFIX = "\\$"; // Thymeleaf Standard Expression Syntax for Variable
    private static final String BASIC_EXPRESSION = "\\{(.*?)\\}";
    protected static final String EVENT_TEMPLATE_M_DO_ACTION = "_M.doAction(null, `%s`)";
    protected static final String QUERY_SELECTOR="'${document.querySelector('%s').value}'";
    protected static final Pattern CTX_ATTRIBUTE_VALUE_REGEX = Pattern.compile(VARIABLE_PREFIX + BASIC_EXPRESSION);
    protected static final Pattern CTX_QUERY_SELECTOR_VALUE_REGEX = Pattern.compile(QUERY_SELECTOR_PREFIX + BASIC_EXPRESSION);

    protected final String eventName;
    protected final String eventTemplate;

    protected JSEventAttributeProcessor(String attributeName, String eventName, String eventTemplate, int precedence) {
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

    protected String replaceElementValues(ITemplateContext context, IProcessableElementTag tag, String attributeValue) {
        if(attributeValue.contains(QUERY_SELECTOR_PREFIX)) {
            Matcher elementValueMatcher = CTX_QUERY_SELECTOR_VALUE_REGEX.matcher(attributeValue);
            while (elementValueMatcher.find()) {
                String querySelector = elementValueMatcher.group(1);
                String replaceValue = QUERY_SELECTOR.formatted(querySelector);
                attributeValue = attributeValue.replace(elementValueMatcher.group(), replaceValue);
            }
        }
        return attributeValue;
    }

    protected String replaceAttributeValues(ITemplateContext context, IProcessableElementTag tag, String attributeValue){
        Matcher matcher = CTX_ATTRIBUTE_VALUE_REGEX.matcher(attributeValue);
        while (matcher.find()) {
            String replaceValue = matcher.group(1);
            attributeValue = attributeValue.replace(matcher.group(), context.getVariable(replaceValue).toString());
        }
        return attributeValue;
    }
}