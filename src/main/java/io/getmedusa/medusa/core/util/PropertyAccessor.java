package io.getmedusa.medusa.core.util;

import org.springframework.expression.spel.standard.SpelExpressionParser;

public abstract class PropertyAccessor {

    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    public static <T> T getValue(String expression, Object object, Class<T> cast) {
        return (T) SPEL_EXPRESSION_PARSER.parseExpression(expression).getValue(object);
    }

    public static String getValue(String expression, Object object) {
        return "" + SPEL_EXPRESSION_PARSER.parseExpression(expression).getValue(object); // use string concatenation to avoid ClassCastException
    }

    public static String getValue(String expression) {
        return SPEL_EXPRESSION_PARSER.parseExpression(expression).getValue().toString();
    }

}
