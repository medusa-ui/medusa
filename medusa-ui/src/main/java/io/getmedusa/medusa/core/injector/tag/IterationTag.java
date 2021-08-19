package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.util.ExpressionEval;
import io.getmedusa.medusa.core.util.IdentifierGenerator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IterationTag {

    private static final String TAG_EACH = "[$each]";
    private static final String TAG_THIS_EACH = "[$this.each]";

    //FYI - DOTALL means 'multiline', ie across newline/line breaks
    private final Pattern propertyPattern =  Pattern.compile("\\[\\$each\\.(.{0,999}])", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private final Pattern blockPattern = Pattern.compile("\\[\\$foreach .{1,999}].*?\\[\\$end for]", Pattern.DOTALL);

    public InjectionResult injectWithVariables(InjectionResult injectionResult, Map<String, Object> variables) {
        String html = injectionResult.getHtml();

        //find all the foreaches, split them up and order them as deepest-first
        final Set<ForEachElement> depthElements = buildDepthElements(html);

        for(ForEachElement depthElement : depthElements) {
            ForEachElement parent = depthElement.parent;
            if(parent != null) {
                //build only the templates for the deeper setups
                final String templateReplacement = buildBlockReplacement(depthElement, variables, true);
                parent.blockHTML = parent.blockHTML.replace(depthElement.blockHTML, templateReplacement);
                parent.innerHTML = parseInnerBlock(parent.blockHTML);
            } else {
                //update actual html and execute, once, on the templates
                html = html.replace(depthElement.replaceHTML, buildBlockReplacement(depthElement, variables, false));
            }
        }

        injectionResult.setHtml(html);
        return injectionResult;
    }

    //code for replacing a foreach block
    private String buildBlockReplacement(ForEachElement element, Map<String, Object> variables, boolean onlyTemplate) {
        final String block = element.blockHTML;
        final String blockInner = element.innerHTML;

        final String templateID = IdentifierGenerator.generateTemplateID(block);
        String template = "\n<template m-id=\"" + templateID + "\">\n" + blockInner + "\n</template>\n";
        StringBuilder iterations = new StringBuilder(template);

        final String condition = parseCondition(block);
        IterationRegistry.getInstance().add(templateID, condition);
        if(!onlyTemplate) {
            List<String> divs = turnTemplateIntoDiv(variables, blockInner, templateID, condition);
            for (String div : divs) iterations.append(div);
        }
        return iterations.toString();
    }

    //specifically for turning templates with conditions into concrete divs
    private List<String> turnTemplateIntoDiv(Map<String, Object> variables, String blockInner, String templateID, String condition) {
        List<String> divs = new ArrayList<>();
        Object conditionParsed = parseCondition(condition, variables);
        if (conditionParsed instanceof Collection) {
            //a true 'foreach', the 'each' becomes each element of the collection
            @SuppressWarnings("unchecked") Object[] iterationCondition = ((Collection<Object>)conditionParsed).toArray();
            for (int i = 0; i < iterationCondition.length; i++) {
                divs.add(buildDivElem(blockInner, templateID, i, iterationCondition[i]));
            }
        } else {
            //if only a single value, we don't need real 'foreach' but rather a regular loop
            //the 'each' then becomes an index
            for (int i = 0; i < (int) conditionParsed; i++) {
                divs.add(buildDivElem(blockInner, templateID, i, i));
            }
        }
        return divs;
    }

    private Set<ForEachElement> buildDepthElements(String html) {
        final Set<ForEachElement> depthElements = new TreeSet<>();
        Matcher matcher = buildBlockMatcher(html);
        while (matcher.find()) {
            ForEachElement element = new ForEachElement();
            element.blockHTML = matcher.group();
            element.replaceHTML = element.blockHTML;
            element.innerHTML = parseInnerBlock(element.blockHTML);
            element.depth = 0;
            element.parent = null;
            depthElements.add(element);

            seekDeeperForeachElements(depthElements, element);
        }
        return depthElements;
    }

    private void seekDeeperForeachElements(Set<ForEachElement> depthElements, ForEachElement element) {
        Matcher deeperMatcher = buildBlockMatcher(element.innerHTML);
        while(deeperMatcher.find()) {
            ForEachElement deeperElement = new ForEachElement();
            deeperElement.blockHTML = deeperMatcher.group();
            deeperElement.innerHTML = parseInnerBlock(deeperElement.blockHTML);
            deeperElement.depth = element.depth+1;
            deeperElement.parent = element;
            depthElements.add(deeperElement);

            seekDeeperForeachElements(depthElements, deeperElement);
        }
    }

    private String buildDivElem(String blockInner, String templateID, int index, Object eachValue) {
        final String innerBlock = parseLine(blockInner, eachValue);
        return "\n<div index=\"" + index + "\" template-id=\"" + templateID + "\">\n" + innerBlock + "\n</div>\n";
    }

    private String parseLine(final String line, Object value) {
        String result = line.replace(TAG_EACH, TAG_THIS_EACH);
        result = result.replace(TAG_THIS_EACH, value.toString());

        Matcher matcher = propertyPattern.matcher(result);
        while (matcher.find()) {
            final String replace = matcher.group(); // $[each.property]
            final String group = matcher.group(1);  // property]
            final String property = group.substring(0, group.length()-1);  // property
            result = result.replace(replace, ExpressionEval.evalObject(property, value));
        }
        return result;
    }

    private Object parseCondition(String condition, Map<String, Object> variables) {
        final Object variable = variables.getOrDefault(condition, new ArrayList<>());
        if(variable == null) return Collections.emptyList();
        return variable;
    }

    protected Matcher buildBlockMatcher(String html) {
        return blockPattern.matcher(html);
    }

    protected String parseInnerBlock(String block) {
        return block.substring(block.indexOf("]") + 1, block.lastIndexOf("[$end for]"));
    }

    protected String parseCondition(String block) {
        return block.substring("[$foreach".length(), block.indexOf("]")).trim().substring(1); //TODO error if condition does not start with $
    }

    class ForEachElement implements Comparable<ForEachElement> {

        ForEachElement parent;
        String replaceHTML; //only the root element has html that needs to be replaced
        String blockHTML; //block including the foreach block and condition
        String innerHTML; //block within the foreach
        int depth;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ForEachElement)) return false;
            ForEachElement element = (ForEachElement) o;
            return depth == element.depth && blockHTML.equals(element.blockHTML);
        }

        @Override
        public int hashCode() {
            return Objects.hash(blockHTML, depth);
        }

        @Override
        public int compareTo(ForEachElement elem) {
            return Integer.compare(elem.depth, depth);
        }
    }
}
