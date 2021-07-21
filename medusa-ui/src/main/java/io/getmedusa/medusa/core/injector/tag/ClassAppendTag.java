package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.ConditionalClassRegistry;
import io.getmedusa.medusa.core.util.ExpressionEval;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassAppendTag {

    public static final Pattern patternClassAppendFull = Pattern.compile("(class=\".*?\".*?)?m-class-append=((\".*?\")|('.*?'))(.*?class=\".*?\")?", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternClass = Pattern.compile("class=\".*?\"", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternClassAppend = Pattern.compile("m-class-append=((\".*?\")|('.*?'))", Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult html, Map<String, Object> variables) {
        String htmlString = html.getHtml();

        Matcher matcherFull = patternClassAppendFull.matcher(htmlString);
        while (matcherFull.find()) {
            ClassAppendingDetermination classesToAppend = determineClassesToAppend(variables, matcherFull.group());
            htmlString = rememberBaseClass(htmlString, matcherFull.group(), classesToAppend);
            htmlString = htmlString.replaceAll(patternClassAppend.pattern(), "");
        }

        html.setHtml(htmlString);
        return html;
    }

    private ClassAppendingDetermination determineClassesToAppend(Map<String, Object> variables, String stringToMatch) {
        Matcher matcherClass = patternClassAppend.matcher(stringToMatch);
        String classesToAppend = "";
        ClassAppendingDetermination determination = new ClassAppendingDetermination();
        if (matcherClass.find()) {
            String classAppendCondition = matcherClass.group().substring(16, matcherClass.group().length()-1);
            determination.id = ConditionalClassRegistry.getInstance().add(classAppendCondition);
            classesToAppend = ExpressionEval.eval(classAppendCondition, variables);
        }
        determination.classesToAppend = classesToAppend;
        return determination;
    }

    private String rememberBaseClass(String htmlString, String matcher, ClassAppendingDetermination determination) {
        Matcher matcherClass = patternClass.matcher(matcher);
        String clazz = "class=\"\"";
        if (matcherClass.find()) {
            clazz = matcherClass.group();
        } else {
            final String replacement = clazz + " m-class-append";
            htmlString = htmlString.replace("m-class-append", replacement);
            matcher = matcher.replace("m-class-append", replacement);
            return rememberBaseClass(htmlString, matcher, determination);
        }

        clazz = clazz.substring(0 , clazz.length()-1) + " " + determination.classesToAppend + "\"";
        clazz = clazz + " " + matcherClass.group().replace("class=", "data-base-class=");
        clazz += " data-from=\"" + determination.id + "\"";
        htmlString = htmlString.replaceAll(matcherClass.group(), clazz);

        return htmlString;
    }

    static class ClassAppendingDetermination {

        public String classesToAppend;
        public String id;

    }
}

