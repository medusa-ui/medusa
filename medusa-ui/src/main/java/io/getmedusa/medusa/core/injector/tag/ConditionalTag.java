package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.ConditionalRegistry;
import io.getmedusa.medusa.core.util.ExpressionEval;
import io.getmedusa.medusa.core.util.IdentifierGenerator;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalTag {
    private static final ConditionalRegistry CONDITIONAL_REGISTRY = ConditionalRegistry.getInstance();

    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        //TODO conditionItem vs conditionString? if needed, be explicit
        //<m:if condition="innerItem" eq="a">
        //    <p>A</p>
        //    <m:elseif conditionItem="innerItem" eq="b">
        //
        //    </m:elseif>
        //    <m:else>
        //
        //    </m:else>
        //</m:if>
        //becomes
        //

        Elements conditionalElements = result.getDocument().getElementsByTag(TagConstants.CONDITIONAL_TAG);
        for(Element conditionalElement : conditionalElements) {
            Object conditionItemValue = getConditionItemValue(variables, conditionalElement);

            boolean isVisible = false;
            if(conditionalElement.hasAttr(TagConstants.CONDITIONAL_TAG_EQUALS)) {
                Object comparisonItemValue = getComparisonItemValue(variables, conditionalElement);
                System.out.println(conditionItemValue + " vs " + comparisonItemValue);
                isVisible = conditionItemValue.equals(comparisonItemValue);
            }

            conditionalElement.replaceWith(createWrapper(conditionalElement, isVisible));
        }

        return result;
    }

    private Node createWrapper(Element element, boolean isVisible) {
        Element node = new Element(Tag.valueOf("div"), "");
                //.attr(TagConstants.M_ID, templateID);

        final String ifID = IdentifierGenerator.generateIfID(element.html());
        final String condition = "1 == 1"; //TODO
        CONDITIONAL_REGISTRY.add(ifID, condition);

        if(!isVisible) {
            node.attr("style", "display:none;");
        }

        node.appendChildren(element.children());

        return node;
    }

    private Object getComparisonItemValue(Map<String, Object> variables, Element conditionalElement) {
        final String comparisonItem = conditionalElement.attr(TagConstants.CONDITIONAL_TAG_EQUALS);
        Object comparisonItemValue = ExpressionEval.evalItemAsObj(comparisonItem, variables);
        if(null == comparisonItemValue) comparisonItemValue = comparisonItem;
        return comparisonItemValue;
    }

    private Object getConditionItemValue(Map<String, Object> variables, Element conditionalElement) {
        final String conditionItem = conditionalElement.attr(TagConstants.CONDITIONAL_TAG_CONDITION_ATTR);
        Object conditionItemValue = ExpressionEval.evalItemAsObj(conditionItem, variables);
        if(null == conditionItemValue) conditionItemValue = conditionItem;
        return conditionItemValue;
    }

    public static final Pattern patternIfStart = Pattern.compile("\\[\\$if\\(.+?]", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternElse = Pattern.compile("\\[\\$ ?else ?(if\\(.+?)?]", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternIfEnd = Pattern.compile("\\[\\$end if]", Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult html, Map<String, Object> variables) {
        final List<String> ifBlocks = determineIfBlocksWithNoInnerBlocks(html.getHTML());

        for(String ifBlock : ifBlocks){
            Set<String> conditions = new HashSet<>();

            final String conditionParsed = parseCondition(ifBlock);
            conditions.add(conditionParsed);
            handleIfBlock(html, variables, ifBlock, conditionParsed);

            final List<String> elseParts = patternMatchAll(patternElse, ifBlock);
            for(String elsePart : elseParts) {
                if(elsePart.contains("if(")) {
                    String elseCondition = parseCondition(elsePart);
                    handleIfBlock(html, variables, elsePart, combineConditions(conditions) + " && " + elseCondition);
                    conditions.add(elseCondition);
                } else {
                    handleIfBlock(html, variables, elsePart, combineConditions(conditions));
                }
            }
        }

        //if any still present, we're dealing with inner ifs and need to parse these again
        if(html.getHTML().contains("[$if(")) return injectWithVariables(html, variables);

        return html;
    }

    private List<String> determineIfBlocksWithNoInnerBlocks(String html) {
        List<String> matches = new ArrayList<>();
        Matcher matcherIfStart = patternIfStart.matcher(html);
        while (matcherIfStart.find()) {
            final String firstPartSplitIf = html.substring(matcherIfStart.end());
            Matcher matcherIfEnd = patternIfEnd.matcher(firstPartSplitIf);
            if (matcherIfEnd.find()) {
                final String htmlSplit = firstPartSplitIf.substring(0, matcherIfEnd.start());

                if(!htmlSplit.contains("[$if")) {
                    final String ifBlock = html.substring(matcherIfStart.start(), matcherIfStart.end() + matcherIfEnd.end());
                    matches.add(ifBlock);
                }
            }
        }
        return matches;
    }

    private String combineConditions(Set<String> conditions) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        String appender = "";
        for(String condition : conditions) {
            builder.append(appender);
            builder.append("!(");
            builder.append(condition);
            builder.append(")");
            appender = " && ";
        }
        builder.append(")");
        return builder.toString();
    }

    private void handleIfBlock(InjectionResult html, Map<String, Object> variables, String ifBlock, String conditionParsed) {
        final String divId = IdentifierGenerator.generateIfID(ifBlock);

        CONDITIONAL_REGISTRY.add(divId, conditionParsed);

        String wrapperStart = "<div class=\""+divId+"\">";
        String wrapperEnd = "</div>";
        if(!CONDITIONAL_REGISTRY.evaluate(divId, variables)) {
            wrapperStart = wrapperStart.replace(">", " style=\"display:none;\">");
        }

        if(ifBlock.startsWith("[$else")) {
            //html.replace(ifBlock, wrapperEnd + ifBlock.replaceFirst(patternElse.pattern(), wrapperStart).replace("[$end if]", wrapperEnd));
        } else {
            //html.replace(ifBlock, ifBlock.replaceFirst(patternIfStart.pattern(), wrapperStart).replace("[$end if]", wrapperEnd));
        }
    }

    protected String parseCondition(String ifBlock) {
        String ifStart = patternMatch(patternIfStart, ifBlock);
        if(ifStart == null && ifBlock.contains("$else")) {
            ifStart = patternMatch(patternElse, ifBlock);
        }
        if(ifStart == null) throw new IllegalStateException("If block must contain a condition");
        return ifStart.substring(ifStart.indexOf("if(") + 3, ifStart.length()-2).trim();
    }

    protected String patternMatch(Pattern pattern, String htmlToMatch) {
        Matcher matcher = pattern.matcher(htmlToMatch);
        if(matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    protected List<String> patternMatchAll(Pattern pattern, String htmlToMatch) {
        List<String> matches = new ArrayList<>();
        Matcher matcher = pattern.matcher(htmlToMatch);
        while (matcher.find()) {
            matches.add(matcher.group(0));
        }
        return matches;
    }
}
