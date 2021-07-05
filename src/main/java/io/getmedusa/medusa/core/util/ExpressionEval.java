package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChange;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ExpressionEval {

    protected static final Pattern pattern = Pattern.compile("\\$\\w+((-|\\.)\\w+)*", Pattern.CASE_INSENSITIVE);

    public static boolean eval(String condition) {
        boolean isVisible;
        try {
            IExpressionEvaluator ee = CompilerFactoryFactory.getDefaultCompilerFactory(ClassLoader.getSystemClassLoader()).newExpressionEvaluator();
            ee.setExpressionType(Boolean.class);
            ee.cook(condition);
            isVisible = (Boolean) ee.evaluate();
        } catch (Exception e) {
            throw new RuntimeException("Cannot eval '"+condition+"'", e);
        }
        return isVisible;
    }

    public static String eval(String expressionToInterpret, Map<String, Object> variables) {
        Matcher matcher = pattern.matcher(expressionToInterpret);
        while (matcher.find()) {
            expressionToInterpret = expressionToInterpret.replace(matcher.group(), interpretValue(matcher.group(), variables));
        }
        if(isStillAnExpressionToInterpret(expressionToInterpret)) {
            return SpelExpressionParserHelper.getStringValue(expressionToInterpret);
        } else {
            return expressionToInterpret;
        }
    }

    private static boolean isStillAnExpressionToInterpret(String expressionToInterpret) {
        return expressionToInterpret.contains(" ? ");
    }

    private static String interpretValue(String value, Map<String, Object> variables) {
        if(value.startsWith("$")) value = value.substring(1);
        if(!variables.containsKey(value)) {
            if(value.contains(".")) {
                String[] variableKeySplit = value.split("\\.", 2);
                return SpelExpressionParserHelper.getStringValue(variableKeySplit[1], variables.get(variableKeySplit[0]));
            }
        } else {
            return variables.get(value).toString();
        }
        return value;
    }

    public static String evalObject(String property, Object value) {
        return SpelExpressionParserHelper.getStringValue(property, value);
    }

    public static List<DOMChange> evalEventController(String event, UIEventController eventController) {
        return SpelExpressionParserHelper.getValue(event, eventController);
    }

    public static List<String> findVariablesPresent(String toCheck) {
        List<String> variables = new ArrayList<>();
        Matcher matcher = pattern.matcher(toCheck);
        while (matcher.find()) variables.add(matcher.group().substring(1));
        return variables;
    }
}
