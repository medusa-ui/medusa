package io.getmedusa.medusa.core.tags.meta;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.inline.StandardHTMLInliner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.*;
import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.precedence;

public abstract class JSEventAttributeProcessor extends AbstractAttributeTagProcessor {
    protected static final String EVENT_TEMPLATE_M_DO_ACTION = "_M.doAction(null, '%s')";
    protected static final Pattern CTX_VALUE_REGEX = Pattern.compile("\\$\\{(.*)\\}");
    protected static final Pattern CTX_ATTRIBUTE_VALUE_REGEX = Pattern.compile("\\$\\{(.*)\\}");

    protected static final Pattern CTX_ELEMENT_VALUE_REGEX = Pattern.compile("#(\\w*)");
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
        if(attributeValue.contains("#")) {
            Matcher elementValueMatcher = CTX_ELEMENT_VALUE_REGEX.matcher(attributeValue);
            context.getConfiguration();
            Document document = Jsoup.parse(context.getTemplateData().getTemplate());
            while (elementValueMatcher.find()) {
                String id = elementValueMatcher.group(1);
                Element element = null;
                String replaceValue = "";
                if(id.equals("this")) {
                    replaceValue = tag.getAttributeMap().getOrDefault("value","");
                } else {
                    element = document.getElementById(id);
                    if (element.hasAttr("value")) {
                        replaceValue = element.attr("value");
                    } else {
                        replaceValue = element.text();
                    }
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
            attributeValue = attributeValue.replace(matcher.group(), context.getVariable(replaceValue).toString());
        }
        return attributeValue;
    }

    protected String format(String source) {
        return source.replace("'","\\'");
    }
}