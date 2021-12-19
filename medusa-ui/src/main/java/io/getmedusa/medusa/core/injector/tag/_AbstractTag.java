package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class _AbstractTag {

    abstract String tagValue();
    abstract String pattern();
    abstract String substitutionLogic(String fullMatch, String tagContent);

    protected final Pattern pattern = Pattern.compile(pattern(), Pattern.CASE_INSENSITIVE);

    protected Matcher buildMatcher(String html) {
        return pattern.matcher(html);
    }

    protected String standardPattern() {
        return "("+tagValue() + "=\".+?\")|("+tagValue() + "='.+?')";
    }


    /**
     * Replaces the m-tags in HTML with usable HTML tags on pageload
     * @param result
     * @return
     */
    public InjectionResult inject(InjectionResult result) {
        Matcher matcher = buildMatcher(result.getHTML());

        final Map<String, String> replacements = new HashMap<>();
        while (matcher.find()) {
            final String fullMatch = matcher.group(0);
            replacements.put(fullMatch, substitutionLogic(fullMatch, fullMatchToTagContent(fullMatch)));
        }

        for(Map.Entry<String, String> entrySet : replacements.entrySet()) {
            //result = result.replace(entrySet.getKey(),  entrySet.getValue());
        }
        return result;
    }

    public String fullMatchToTagContent(String fullMatch) {
        String tagContent = fullMatch;
        if(tagContent.contains("=")) tagContent = tagContent.replaceFirst(tagValue() + "=", "");
        if(tagContent.startsWith("\"")) tagContent = tagContent.substring(1, tagContent.length() - 1);
        if(tagContent.startsWith("[$")) tagContent = tagContent.substring(2, tagContent.length() - 1);
        if(tagContent.startsWith("'")) tagContent = tagContent.substring(1, tagContent.length() - 1).replace("\"","'");
        return tagContent.trim();
    }

    public InjectionResult inject(String html) {
        InjectionResult result = null;// new InjectionResult(html);
        return inject(result);
    }
}
