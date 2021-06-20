package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.ConditionalRegistry;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalTag {
    private static final ConditionalRegistry CONDITIONAL_REGISTRY = ConditionalRegistry.getInstance();

    public static final Pattern patternFullIf = Pattern.compile("\\[\\$if\\(.+].*?\\[\\$end]", Pattern.DOTALL);
    public static final Pattern patternIfStart = Pattern.compile("\\[\\$if\\(.+]", Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult html, Map<String, Object> variables) {
        final String ifBlock = patternMatch(patternFullIf, html.getHtml()); //TODO ability to handle multiple if statements
        if(null != ifBlock) {
            final String conditionParsed = parseCondition(ifBlock);
            final String divId = generateDivID(ifBlock);

            CONDITIONAL_REGISTRY.add(divId, conditionParsed);
            String wrapperStart = "<div id=\""+divId+"\">";
            String wrapperEnd = "</div>";
            if(!CONDITIONAL_REGISTRY.evaluate(divId, variables)) {
                wrapperStart = wrapperStart.replace(">", " style=\"display:none;\">");
            }
            html.replace(ifBlock, ifBlock.replaceFirst(patternIfStart.pattern(), wrapperStart).replace("[$end]", wrapperEnd));
        }

        return html;
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
}
