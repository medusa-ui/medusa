package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.expression.spel.SpelEvaluationException;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
            Object val = interpretValue(itemToEval, variables, true);
            return val.toString();
        } catch (SpelEvaluationException e) {
            return null;
        }
    }

    public static Object evalItemAsObj(String itemToEval, Map<String, Object> variables) {
        if(WrapperUtils.isQuoted(itemToEval)) {
            return itemToEval;
        }
        return interpretValue(itemToEval, variables, true);
    }

    public static Object evalItemAsObjForConditional(String itemToEval, Map<String, Object> variables) {
        if(WrapperUtils.isQuoted(itemToEval)) {
            return itemToEval;
        }
        return interpretValue(itemToEval, variables, false);
    }

    private static Object interpretValue(String value, Map<String, Object> variables, boolean exceptionOnFullObject) {
        if (!variables.containsKey(value)) {
            if(value.contains(".") || (value.contains("[") && value.contains("]"))) {
                String[] variableKeySplit = value.split("[.\\[]", 2);
                Object objValue = variables.get(variableKeySplit[0]);
                String restOfKey = resolveRestOfKey(variableKeySplit[1], variables);
                Object subValue = SpelExpressionParserHelper.getValue(restOfKey, objValue);
                if(exceptionOnFullObject) {
                    return validateCompatibleValue(subValue, value);
                } else {
                    return subValue;
                }
            }
        } else {
            if(exceptionOnFullObject) {
                return validateCompatibleValue(variables.get(value), value);
            } else {
                return variables.get(value);
            }
        }
        return value;
    }

    private static Object validateCompatibleValue(Object objValue, String originalLookupValue) {
        try {
            boolean isValid;
            if (objValue.getClass().getComponentType() != null) {
                isValid = objValue.getClass().getComponentType().getPackage().getName().startsWith("java.");
            } else {
                isValid = objValue.getClass().getPackage().getName().startsWith("java.");
            }

            if(isValid) {
                return objValue;
            } else {
                throw unableToRenderFullObjectException(originalLookupValue, objValue.getClass());
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static String resolveRestOfKey(String path, Map<String, Object> variables) {
        if(path.endsWith("]")) {
            path = "[" + path;
            for(String pathPart : path.split("\\[")) {
                if(!pathPart.isEmpty()) {
                    pathPart = pathPart.replace("]", "");
                    final String interpretValue = interpretValue(pathPart, variables, true).toString();
                    path = path.replaceAll("\\["+pathPart+"]", "[" + interpretValue + "]");
                }
            }
        }

        final int length = path.length() - 1;
        if(1 < length) {
            final String subPath = path.substring(1, length);
            if (subPath.contains("[")) {
                String[] variableKeySplit = subPath.split("[.\\[]", 2);
                Object objValue = variables.get(variableKeySplit[0]);
                if(objValue != null) {
                    String restOfKey = resolveRestOfKey(variableKeySplit[1], variables);
                    Object subValue = SpelExpressionParserHelper.getValue(restOfKey, objValue);
                    return "[" + subValue.toString() + "]";
                }
            }
        }

        return path;
    }

    private static IllegalArgumentException unableToRenderFullObjectException(String value, Class<? extends Object> objValueClass) {
        return new IllegalArgumentException("HTML was asked to visualize an entire object [" + value + ", " + objValueClass.getSimpleName() + "] instead of an object's property. An object is only known on the serverside, and cannot be rendered directly in the HTML. Try rendering a property of this object instead. For example: If you are trying to render a Person object with [person], instead render the person's name using [person.name]");
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

    public static boolean isNumber(String parameter) {
        try {
            new BigDecimal(parameter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBoolean(String parameter) {
        return Boolean.TRUE.toString().equals(parameter) || Boolean.FALSE.toString().equals(parameter);
    }
}
