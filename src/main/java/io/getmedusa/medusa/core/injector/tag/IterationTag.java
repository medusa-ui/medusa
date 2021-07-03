package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.util.IdentifierGenerator;
import io.getmedusa.medusa.core.util.SpelExpressionParserHelper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IterationTag {

    private static final String $_EACH = "[$each]";
    private final Pattern propertyPattern =  Pattern.compile("\\[\\$each\\.(.*?])", Pattern.DOTALL + Pattern.CASE_INSENSITIVE);
    private final Pattern blockPattern = Pattern.compile("\\[\\$foreach .+?].*?\\[\\$end for]", Pattern.DOTALL);

    public InjectionResult injectWithVariables(InjectionResult injectionResult, Map<String, Object> variables) {
        String html = injectionResult.getHtml();

        Matcher matcher = buildBlockMatcher(html);
        while (matcher.find()) {
            String block = matcher.group(0);
            String blockInner = parseInnerBlock(block);

            final String templateID = IdentifierGenerator.generateTemplateID(blockInner);
            String template = "\n<template id=\"" + templateID + "\">\n" + blockInner + "\n</template>\n";
            StringBuilder iterations = new StringBuilder(template);

            String condition = parseCondition(block);
            IterationRegistry.getInstance().add(templateID, condition);

            Object[] iterationCondition = parseCondition(condition, variables).toArray();
            for (int i = 0; i < iterationCondition.length; i++) {
                Object eachValue = iterationCondition[i];
                String iteration = "\n<div index=\"" + i + "\" template-id=\"" + templateID + "\">\n" + parseLine(blockInner, eachValue) + "\n</div>\n";
                iterations.append(iteration);
            }
            html = html.replace(block, iterations.toString());
        }

        injectionResult.setHtml(html);
        return injectionResult;
    }

    private String parseLine(String line, Object value ) {
        String result = line;
        result = line.replace($_EACH, value.toString());

        Matcher matcher = propertyPattern.matcher(result);
        while (matcher.find()) {
            final String replace = matcher.group(); // $[each.property]
            final String group = matcher.group(1);  // property]
            final String property = group.substring(0, group.length()-1);  // property
            result = result.replace(replace, SpelExpressionParserHelper.getStringValue(property,value));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> parseCondition(String condition, Map<String, Object> variables) {
        final Object variable = variables.getOrDefault(condition, new ArrayList<>());
        if(variable == null) return Collections.emptyList();
        if (variable instanceof Collection) {
            return (Collection<Object>) variable;
        } else {
            return Collections.singletonList(variable);
        }
    }

    protected Matcher buildBlockMatcher(String html) {
        return blockPattern.matcher(html);
    }

    protected String parseInnerBlock(String block) {
        return block.substring(block.indexOf("]") + 1, block.indexOf("[$end for]"));
    }

    protected String parseCondition(String block) {
        return block.substring("[$foreach".length(), block.indexOf("]")).trim().substring(1);
    }

}
