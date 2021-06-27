package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.util.IdentifierGenerator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IterationTag {

    private final Pattern blockPattern = Pattern.compile("\\[\\$foreach .+?].*?\\[\\$end]", Pattern.DOTALL);

    public InjectionResult injectWithVariables(InjectionResult injectionResult, Map<String, Object> variables) {
        String html = injectionResult.getHtml();

        Matcher matcher = buildBlockMatcher(html);
        while (matcher.find()) {
            String block = matcher.group(0);
            String blockInner = parseInnerBlock(block);

            final String templateID = IdentifierGenerator.generateTemplateID();
            String template = "\n<template id=\"" + templateID + "\">\n" + blockInner + "\n</template>\n";
            StringBuilder iterations = new StringBuilder(template);

            String condition = parseCondition(block);
            IterationRegistry.getInstance().add(templateID, condition);

            Collection<Object> iterationCondition = parseCondition(condition, variables);
            for (int i = 0; i < iterationCondition.size(); i++) {
                String iteration = "\n<div index=\"" + i + "\" template-id=\"" + templateID + "\">\n" + blockInner + "\n</div>\n";
                iterations.append(iteration);
            }
            html = html.replace(block, iterations.toString());
        }

        injectionResult.setHtml(html);
        return injectionResult;
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> parseCondition(String condition, Map<String, Object> variables) {
        final Object variable = variables.getOrDefault(condition, new ArrayList<>());
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
        return block.substring(block.indexOf("]") + 1, block.indexOf("[$end]"));
    }

    protected String parseCondition(String block) {
        return block.substring("[$foreach".length(), block.indexOf("]")).trim().substring(1);
    }

}
