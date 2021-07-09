package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.util.ExpressionEval;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueTag {

    private static final char DOUBLE_QUOTE = '\"';
    protected final Pattern pattern = Pattern.compile("\\[\\$(?!each|if|else|end).*?\\]", Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult result, Map<String, Object> variables) {
        String html = result.getHtml();
        html = replaceTitle(html, variables);

        Matcher matcher = pattern.matcher(html);
        Map<String, String> replacements = new HashMap<>();

        while (matcher.find()) {
            Context context = determineContext(html, matcher);

            final String match = matcher.group(0);
            final String searchKey = toSearchKey(match);
            final String value = ExpressionEval.eval(searchKey, variables);
            final String variableKey = match.substring(2, match.length() - 1).trim();

            if(context.equals(Context.AS_ATTRIBUTE)) {
                final String replacement = value + DOUBLE_QUOTE + " from-value=\"" + variableKey + "\"";
                replacements.put(match + DOUBLE_QUOTE, replacement);
            } else if(context.equals(Context.AS_WRAPPED_IN_TAG)) {
                final String replacement = " from-value=\"" + variableKey + "\">" + value;
                replacements.put(">" + match, replacement);
            } else {
                replacements.put(match, "<span from-value=\"" + variableKey + "\">"+value+"</span>");
            }
        }

        for(Map.Entry<String, String> replacement : replacements.entrySet()) {
           while(html.contains(replacement.getKey())) {
                html = html.replace(replacement.getKey(), replacement.getValue());
            }
        }

        result.setHtml(html);
        return result;
    }

    private String toSearchKey(String match) {
        String key = match.substring(1, match.length() - 1).trim();
        if(key.contains("$ ")) {
            key = key.replace("$ ", "$");
        }
        return key;
    }

    private String replaceTitle(String html, Map<String, Object> variables) {
        Pattern titlePattern = Pattern.compile("<title>.+</title>", Pattern.CASE_INSENSITIVE);
        Matcher titleMatcher = titlePattern.matcher(html);

        if (titleMatcher.find()) {
            final String title = titleMatcher.group(0);
            String titleCopy = title;

            Matcher matcher = pattern.matcher(html);

            while (matcher.find()) {
                final String match = matcher.group(0);
                final String variableKey = match.substring(2, match.length() - 1).trim();
                final Object value = variables.get(variableKey);
                if(value == null) throw new IllegalStateException("Variable key '" + variableKey + "' should either exist or shows an error in internal parsing logic.");
                final String valueAsString = value.toString();
                titleCopy = titleCopy.replace(match, valueAsString);
            }
            html = html.replace(title, titleCopy);
        }

        return html;
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
