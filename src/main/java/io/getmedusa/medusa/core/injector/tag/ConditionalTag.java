package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.ConditionalRegistry;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalTag {
    private static final ConditionalRegistry CONDITIONAL_REGISTRY = ConditionalRegistry.getInstance();

    public static final Pattern patternFullIf = Pattern.compile("\\[\\$if\\(.+].*?\\[\\$end]", Pattern.DOTALL);
    public static final Pattern patternIfStart = Pattern.compile("\\[\\$if\\(.+]", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternElse = Pattern.compile("\\[\\$ ?else ?(if\\(.+)?]", Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult html, Map<String, Object> variables) {
        final List<String> ifBlocks = patternMatchAll(patternFullIf, html.getHtml());

        for(String ifBlock : ifBlocks){
            final String conditionParsed = parseCondition(ifBlock);
            handleIfBlock(html, variables, ifBlock, conditionParsed);

            final List<String> elseParts = patternMatchAll(patternElse, ifBlock);
            for(String elsePart : elseParts) {
                if(elsePart.contains("if(")) {
                    handleIfBlock(html, variables, elsePart, parseCondition(elsePart));
                } else {
                    handleIfBlock(html, variables, elsePart, "!(" + conditionParsed + ")");
                }
            }
        }

        return html;
    }

    private void handleIfBlock(InjectionResult html, Map<String, Object> variables, String ifBlock, String conditionParsed) {
        final String divId = generateDivID(ifBlock);

        CONDITIONAL_REGISTRY.add(divId, conditionParsed);
        String wrapperStart = "<div id=\""+divId+"\">";
        String wrapperEnd = "</div>";
        if(!CONDITIONAL_REGISTRY.evaluate(divId, variables)) {
            wrapperStart = wrapperStart.replace(">", " style=\"display:none;\">");
        }

        if(ifBlock.startsWith("[$else")) {
            html.replace(ifBlock, wrapperEnd + ifBlock.replaceFirst(patternElse.pattern(), wrapperStart).replace("[$end]", wrapperEnd));
        } else {
            html.replace(ifBlock, ifBlock.replaceFirst(patternIfStart.pattern(), wrapperStart).replace("[$end]", wrapperEnd));
        }
    }

    private String generateDivID(String ifBlock) {
        return "if-" + ifBlock.hashCode();
    }

    protected String parseCondition(String ifBlock) {
        String ifStart = patternMatch(patternIfStart, ifBlock);
        if(ifStart == null) throw new IllegalStateException("If block must contain a condition");
        return ifStart.substring(5, ifStart.length()-2).trim();
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
