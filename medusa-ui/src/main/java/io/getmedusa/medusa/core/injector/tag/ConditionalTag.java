package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.ElseIfElement;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.injector.tag.meta.VisibilityDetermination;
import io.getmedusa.medusa.core.injector.tag.meta.VisibilityDetermination.ConditionResult;
import io.getmedusa.medusa.core.registry.ConditionalRegistry;
import io.getmedusa.medusa.core.util.IdentifierGenerator;
import io.getmedusa.medusa.core.util.WrapperUtils;
import org.jsoup.nodes.Element;
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
            handleIfElement(variables, conditionalElement);
        }

        return result;
    }

    private void handleIfElement(Map<String, Object> variables, Element conditionalElement) {
        final ConditionResult mainCondition = VisibilityDetermination.getInstance().determine(variables, conditionalElement);
        final String ifId = generateIfID(conditionalElement, mainCondition.condition());

        //find elements
        final Element elseElement = getElseElement(conditionalElement);
        final List<ElseIfElement> elseIfElements = getElseIfElements(conditionalElement, variables); //pre-wrapped
        final Elements mainElements = filterMainElements(conditionalElement, elseElement, elseIfElements);

        //wrapping
        addFullWrapperAndReplaceMIFElement(conditionalElement, ifId);
        final Element mainElementWrapper = wrapMainElement(mainElements);
        final Element defaultElseWrapper = wrapDefaultElseElement(elseElement);

        //visibility
        final ElseIfElement activeElseIf;
        if(!mainCondition.visible()) {
            activeElseIf = determineActiveElseIf(elseIfElements); //determine which elseIf is relevant, if any
        } else {
            activeElseIf = null; //not relevant, overruled by main node
        }

        hideAll(elseIfElements, activeElseIf);
        if(null == activeElseIf) {
            if (mainCondition.visible()) {
                hide(defaultElseWrapper);
            } else {
                hide(mainElementWrapper);
            }
        } else {
            hide(defaultElseWrapper);
            hide(mainElementWrapper);
        }
    }

    private void hideAll(List<ElseIfElement> elseIfElements, ElseIfElement activeElseIf) {
        for(ElseIfElement e : elseIfElements) {
            if(!e.equals(activeElseIf)) {
                hide(e.getElement());
            }
        }
    }

    private ElseIfElement determineActiveElseIf(List<ElseIfElement> elements) {
        for(ElseIfElement element : elements) if (element.isValid()) return element;
        return null;
    }

    private List<ElseIfElement> getElseIfElements(Element conditionalElement, Map<String, Object> variables) {
        final Elements elements = conditionalElement.getElementsByTag(TagConstants.M_ELSEIF);
        if(elements.isEmpty()) return Collections.emptyList();
        return elements.stream().map(e -> new ElseIfElement(e, variables)).toList();
    }

    private Element addFullWrapperAndReplaceMIFElement(Element conditionalElement, String ifId) {
        final Element fullConditionalWrapper = WrapperUtils.wrapAndReplace(conditionalElement, "m-if-wrapper");
        fullConditionalWrapper.attr(TagConstants.M_IF, ifId);
        return fullConditionalWrapper;
    }

    private Element wrapMainElement(Elements mainElements) {
        return WrapperUtils.wrap(mainElements, "m-if-main");
    }

    private Element wrapDefaultElseElement(Element elseElement) {
        Element defaultElseWrapper = null;
        if(elseElement != null) defaultElseWrapper = WrapperUtils.wrapAndReplace(elseElement, "m-if-default-else");
        return defaultElseWrapper;
    }

    private Elements filterMainElements(Element conditionalElement, Element defaultElseElement, List<ElseIfElement> elseIfElements) {
        List<Element> elementsToFilter = new ArrayList<>();
        if(defaultElseElement != null) elementsToFilter.add(defaultElseElement);
        if(elseIfElements != null) for(ElseIfElement elseIfElement : elseIfElements) elementsToFilter.add(elseIfElement.getElement());
        return new Elements(conditionalElement.children().stream().filter(e -> !elementsToFilter.contains(e)).toList());
    }

    private Element getElseElement(Element conditionalElement) {
        final Elements elements = conditionalElement.getElementsByTag(TagConstants.M_ELSE);
        if(elements.isEmpty()) return null;
        if(elements.size() > 1) throw new IllegalStateException("m:if condition can only have one m:else condition. Use m:else if for multiple conditions.");
        return elements.get(0);
    }

    private String generateIfID(Element element, String condition) {
        final String ifID = IdentifierGenerator.generateIfID(element.html());
        CONDITIONAL_REGISTRY.add(ifID, condition);
        return ifID;
    }

    private Element hide(Element elementToHide) {
        if(elementToHide == null) return null;
        return elementToHide.attr("style", "display:none;");
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
