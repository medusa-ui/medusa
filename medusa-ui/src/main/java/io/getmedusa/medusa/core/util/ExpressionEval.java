package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import org.springframework.expression.spel.SpelEvaluationException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ExpressionEval {

    private ExpressionEval() {}

    protected static final Pattern pattern = Pattern.compile("\\$\\s?\\w+((-|\\.)\\w+)*", Pattern.CASE_INSENSITIVE);

    public static boolean evalAsBool(String expressionToInterpret, Map<String, Object> variables) {
        return Boolean.parseBoolean(eval(expressionToInterpret, variables));
    }

    public static String eval(String expressionToInterpret, Map<String, Object> variables) {
        Matcher matcher = pattern.matcher(expressionToInterpret);
        while (matcher.find()) {
            expressionToInterpret = expressionToInterpret.replace(matcher.group(), evalItemAsString(matcher.group(), variables));
        }
        if (SpelExpressionParserHelper.isExpression(expressionToInterpret)) {
            return SpelExpressionParserHelper.getStringValue(expressionToInterpret);
        } else {
            return expressionToInterpret;
        }
    }

    public static String evalItemAsString(String itemToEval, Map<String, Object> variables) {
        try {
            Object val = interpretValue(itemToEval, variables);
            if (val == null) return null;
            return val.toString();
        } catch (SpelEvaluationException e) {
            return null;
        }
    }

    public static Object evalItemAsObj(String itemToEval, Map<String, Object> variables) {
        return interpretValue(itemToEval, variables);
    }

    private static Object interpretValue(String value, Map<String, Object> variables) {
        if (!variables.containsKey(value)) {
            if (value.contains(".")) {
                String[] variableKeySplit = value.split("\\.", 2);
                Object objValue = variables.get(variableKeySplit[0]);
                Object subValue = SpelExpressionParserHelper.getValue(variableKeySplit[1], objValue);
                if (subValue.getClass().getPackage().getName().startsWith("java.")) {
                    return subValue;
                } else {
                    throw unableToRenderFullObjectException(value, subValue.getClass());
                }
            }
        } else {
            Object objValue = variables.get(value);
            if (objValue.getClass().getPackage().getName().startsWith("java.")) {
                return objValue;
            } else {
                throw unableToRenderFullObjectException(value, objValue.getClass());
            }
        }
        return null;
    }

    private static IllegalArgumentException unableToRenderFullObjectException(String value, Class<? extends Object> objValueClass) {
        return new IllegalArgumentException("HTML was asked to visualize an entire object [$" + value + ", " + objValueClass.getSimpleName() + "] instead of an object's property. An object is only known on the serverside, and cannot be rendered directly in the HTML. Try rendering a property of this object instead. For example: If you are trying to render a Person object with [$person], instead render the person's name using [$person.name]");
    }

    public static String evalObject(String property, Object value) {
        return SpelExpressionParserHelper.getStringValue(property, value);
    }

    public static DOMChanges evalEventController(String event, Object eventController) {
        final Object value = SpelExpressionParserHelper.getValue(escape(event), eventController);
        if(value == null) return DOMChanges.empty();
        if(!(value instanceof DOMChanges)) throw new IllegalArgumentException("The event [" + event + "] was executed but did not complete correctly, because the return value was not [io.getmedusa.medusa.core.injector.DOMChanges], but instead [" + value.getClass().getName() + "]");
        return (DOMChanges) value;
    }

    public static String escape(String raw) {
        try {
            return URLDecoder.decode(raw.replace("%27", "''"), Charset.defaultCharset().displayName());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static ForEachElement evalForEachElement(String path, ForEachElement element) {
        final Object value = SpelExpressionParserHelper.getValue(escape(path), element);
        if(value == null) return null;
        if(!(value instanceof ForEachElement)) throw new IllegalArgumentException("The element [" + element + "] was not a proper for each element, but instead [" + value.getClass().getName() + "]");
        return (ForEachElement) value;
    }
}
