package io.getmedusa.medusa.core.tags.meta;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.*;

public abstract class JSEventAttributeProcessor extends AbstractAttributeTagProcessor {
    //`search('${document.querySelector("input").value}')`
    private final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();
    public static final String QUERY_SELECTOR_PREFIX = ":"; // avoid collisions with existing Thymeleaf Standard Expression Syntax
    public static final String VARIABLE_PREFIX = "\\$"; // Thymeleaf Standard Expression Syntax for Variable
    private static final String BASIC_EXPRESSION = "\\{(.*?)\\}";
    protected static final String EVENT_TEMPLATE_M_DO_ACTION = "_M.doAction(null, `%s`)";
    protected static final String SELECTOR_QUERY ="'${document.querySelector('%s').%s}'";
    protected static final String SELECTOR_THIS_REFERENCE ="'${%s.%s}'";
    protected static final String SELECTOR_DEFAULT_ATTRIBUTE ="value";
    protected static final String SELECTOR_ALLOWED_ATTRIBUTES ="accept alt checked class cols data dir disabled for href id lang list max media min multiple name open placeholder readonly rel required rows rowspan selected span target title type value width wrap";
    protected static final Pattern CTX_ATTRIBUTE_VALUE_REGEX = Pattern.compile(VARIABLE_PREFIX + BASIC_EXPRESSION);
    protected static final Pattern CTX_QUERY_SELECTOR_VALUE_REGEX = Pattern.compile(QUERY_SELECTOR_PREFIX + BASIC_EXPRESSION);

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

    protected String replaceElementValues(ITemplateContext context, IProcessableElementTag tag, String attributeValue) {
        if(attributeValue.contains(QUERY_SELECTOR_PREFIX)) {
            Matcher elementValueMatcher = CTX_QUERY_SELECTOR_VALUE_REGEX.matcher(attributeValue);
            while (elementValueMatcher.find()) {
                String select = SELECTOR_DEFAULT_ATTRIBUTE;
                String replaceValue = "";
                String querySelector = elementValueMatcher.group(1);
                int index = querySelector.lastIndexOf(".");
                // index == -1 => not found, index == 0 => class querySelector
                if(index > 0 && SELECTOR_ALLOWED_ATTRIBUTES.contains(querySelector.substring(index + 1))) {
                    select = querySelector.substring(index + 1);
                    querySelector = querySelector.substring(0, index);
                }
                if(querySelector.startsWith("this")) {
                    replaceValue = SELECTOR_THIS_REFERENCE.formatted(querySelector, select);
                } else {
                    replaceValue = SELECTOR_QUERY.formatted(querySelector, select);
                }
                attributeValue = attributeValue.replace(elementValueMatcher.group(), replaceValue);
            }
        }
        return attributeValue;
    }

    protected String replaceAttributeValues(ITemplateContext context, IProcessableElementTag tag, String attributeValue){
        Matcher matcher = CTX_ATTRIBUTE_VALUE_REGEX.matcher(attributeValue);
        while (matcher.find()) {
            String replaceValue = matcher.group(1);
            attributeValue = attributeValue.replace(matcher.group(), valueOf(context, replaceValue));
        }
        return attributeValue;
    }

    protected String valueOf(ITemplateContext context, String replaceValue){
        if(replaceValue.contains(".")) {
            String expression = replaceValue.substring(replaceValue.indexOf(".") + 1);
            String target = replaceValue.substring(0, replaceValue.indexOf("."));
            return "" + SPEL_EXPRESSION_PARSER.parseExpression(expression).getValue(context.getVariable(target));
        } else {
            return context.getVariable(replaceValue).toString();
        }
    }
}