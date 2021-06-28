package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueTag {

    private static final char DOUBLE_QUOTE = '\"';
    protected final Pattern pattern = Pattern.compile("\\[\\$(?!each).*\\]", Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult result, Map<String, Object> variables) {
        String html = result.getHtml();
        Matcher matcher = pattern.matcher(html);

        Map<String, String> replacements = new HashMap<>();

        while (matcher.find()) {
            Context context = determineContext(html, matcher);

            final String match = matcher.group(0);
            final String variableKey = match.substring(2, match.length() - 1).trim();
            if(!variables.containsKey(variableKey)) continue;
            final String value = variables.get(variableKey).toString();

            if(context.equals(Context.AS_ATTRIBUTE)) {
                final String replacement = value + DOUBLE_QUOTE + " from-value=\"" + variableKey + "\"";
                replacements.put(match + DOUBLE_QUOTE, replacement);
            } else {
                //TODO AS_WRAPPED_IN_TAG
                replacements.put(match, "<span from-value=\"" + variableKey + "\">"+value+"</span>");
            }
        }

        for(Map.Entry<String, String> replacement : replacements.entrySet()) {
            final String key = replacement.getKey().replace("[$", "\\[\\$").replace("]", "\\]");
            html = html.replaceAll(key, replacement.getValue()); //TODO replaceAll feels odd here
        }

        result.setHtml(html);
        return result;
    }

    private Context determineContext(String html, Matcher matcher) {
        char before = html.charAt(matcher.start(0) - 1);
        char after = html.charAt(matcher.end(0));

        if(DOUBLE_QUOTE == before && DOUBLE_QUOTE == after) {
            return Context.AS_ATTRIBUTE;
        } else if('>' == before && '<' == after) {
            return Context.AS_WRAPPED_IN_TAG;
        }

        return Context.AS_PLAIN_VALUE;
    }

    private enum Context {
        AS_ATTRIBUTE,
        AS_WRAPPED_IN_TAG,
        AS_PLAIN_VALUE;
    }
}
