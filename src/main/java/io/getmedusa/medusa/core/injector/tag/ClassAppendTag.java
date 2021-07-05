package io.getmedusa.medusa.core.injector.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.util.ExpressionEval;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassAppendTag {

    public static final Pattern patternClassAppendFull = Pattern.compile("(class=\".*?\".*?)?m-class-append=((\".*?\")|('.*?'))(.*?class=\".*?\")?", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternClass = Pattern.compile("class=\".*?\"", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternClassAppend = Pattern.compile("m-class-append=((\".*?\")|('.*?'))", Pattern.CASE_INSENSITIVE);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public InjectionResult injectWithVariables(InjectionResult html, Map<String, Object> variables) {
        String htmlString = html.getHtml();

        Matcher matcherFull = patternClassAppendFull.matcher(htmlString);
        while (matcherFull.find()) {
            List<String> variablesPresent = ExpressionEval.findVariablesPresent(matcherFull.group());
            String classesToAppend = determineClassesToAppend(variables, matcherFull.group());
            htmlString = rememberBaseClass(htmlString, matcherFull.group(), classesToAppend, variablesPresent);
            htmlString = htmlString.replaceAll(patternClassAppend.pattern(), "");
        }

        html.setHtml(htmlString);
        return html;
    }

    private String determineClassesToAppend(Map<String, Object> variables, String stringToMatch) {
        Matcher matcherClass = patternClassAppend.matcher(stringToMatch);
        String classesToAppend = "";
        if (matcherClass.find()) {
            String classAppendCondition = matcherClass.group().substring(16, matcherClass.group().length()-1);
            classesToAppend = ExpressionEval.eval(classAppendCondition, variables);
        }
        return classesToAppend;
    }

    private String rememberBaseClass(String htmlString, String matcher, String classesToAppend, List<String> variablesPresent) {
        Matcher matcherClass = patternClass.matcher(matcher);
        String clazz = "class=\"\"";
        if (matcherClass.find()) {
            clazz = matcherClass.group();
        } else {
            final String replacement = clazz + " m-class-append";
            htmlString = htmlString.replace("m-class-append", replacement);
            matcher = matcher.replace("m-class-append", replacement);
            return rememberBaseClass(htmlString, matcher, classesToAppend, variablesPresent);
        }

        clazz = clazz.substring(0 , clazz.length()-1) + " " + classesToAppend + "\"";
        clazz = clazz + " " + matcherClass.group().replace("class=", "data-base-class=");
        clazz += " data-from=\"" + toJSONList(variablesPresent) + "\"";
        htmlString = htmlString.replaceAll(matcherClass.group(), clazz);

        return htmlString;
    }

    private String toJSONList(List<String> variablesPresent) {
        try {
            return OBJECT_MAPPER.writeValueAsString(variablesPresent).replace("\"", "'");
        } catch (JsonProcessingException e) {
            return variablesPresent.toString();
        }
    }
}

