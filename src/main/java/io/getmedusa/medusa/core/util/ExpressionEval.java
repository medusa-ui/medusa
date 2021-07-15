package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.annotation.DOMChanges;
import io.getmedusa.medusa.core.annotation.UIEventComponent;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChange;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
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
            expressionToInterpret = expressionToInterpret.replace(matcher.group(), interpretValue(matcher.group(), variables));
        }
        if (SpelExpressionParserHelper.isExpression(expressionToInterpret)) {
            return SpelExpressionParserHelper.getStringValue(expressionToInterpret);
        } else {
            return expressionToInterpret;
        }
    }

    private static String interpretValue(String value, Map<String, Object> variables) {
        if (value.startsWith("$")) value = value.substring(1);
        if (!variables.containsKey(value)) {
            if (value.contains(".")) {
                String[] variableKeySplit = value.split("\\.", 2);
                Object objValue = variables.get(variableKeySplit[0]);
                Object subValue = SpelExpressionParserHelper.getValue(variableKeySplit[1], objValue);
                if (subValue.getClass().getPackage().getName().startsWith("java.")) {
                    return subValue.toString();
                } else {
                    throw unableToRenderFullObjectException(value, subValue.getClass());
                }
            }
        } else {
            Object objValue = variables.get(value);
            if (objValue.getClass().getPackage().getName().startsWith("java.")) {
                return objValue.toString();
            } else {
                throw unableToRenderFullObjectException(value, objValue.getClass());
            }
        }
        return value;
    }

    private static IllegalArgumentException unableToRenderFullObjectException(String value, Class<? extends Object> objValueClass) {
        return new IllegalArgumentException("HTML was asked to visualize an entire object [$" + value + ", " + objValueClass.getSimpleName() + "] instead of an object's property. An object is only known on the serverside, and cannot be rendered directly in the HTML. Try rendering a property of this object instead. For example: If you are trying to render a Person object with [$person], instead render the person's name using [$person.name]");
    }

    public static String evalObject(String property, Object value) {
        return SpelExpressionParserHelper.getStringValue(property, value);
    }

    public static List<DOMChange> evalEventController(String event, UIEventComponent eventController) {
        List<DOMChange> changes = new ArrayList<>();
        if(eventController instanceof UIEventPage) {
            SpelExpressionParserHelper.getValue(event,eventController);
            /* TODO clean + check args */
            Class<? extends UIEventComponent> clazz = eventController.getClass();
            for (Method method : clazz.getDeclaredMethods()){
                if (event.startsWith(method.getName()) && method.isAnnotationPresent(DOMChanges.class)) {
                    String[] props = method.getAnnotation(DOMChanges.class).value();
                    for (String prop : props) {
                        try {
                            Method getter = ReflectionUtil.getter(prop, clazz);
                            if(null != getter) changes.add(new DOMChange(prop, getter.invoke(eventController)));
                        }  catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            changes.addAll(SpelExpressionParserHelper.getValue(escape(event), eventController));
        }
        System.out.println(changes);
        return changes;
    }

    public static String escape(String raw) {
        try {
            return URLDecoder.decode(raw.replace("%27", "''"), Charset.defaultCharset().displayName());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
