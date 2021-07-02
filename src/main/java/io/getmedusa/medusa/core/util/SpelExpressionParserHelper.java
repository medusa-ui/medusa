package io.getmedusa.medusa.core.util;

import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Helper class to parse SPel-Expressions
 */
public abstract class SpelExpressionParserHelper {

    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    /**
     * Retrieve the value from the given object based on the expression
     *
     * @param expression SPel expression
     * @param object rootObject
     * @param <T> return type
     * @return parsed value
     */
    public static <T> T getValue(String expression, Object object) {
        return (T) SPEL_EXPRESSION_PARSER.parseExpression(expression).getValue(object);
    }

    /**
     * Retrieve the value of the given expression
     *
     * @param expression SPel expression
     * @param <T> return type
     * @return parsed value
     */
    public static <T> T getValue(String expression) {
        return (T) SPEL_EXPRESSION_PARSER.parseExpression(expression).getValue();
    }

    /**
     * Retrieve the value as String from the given object based on the expression
     *
     * @param expression SPel expression
     * @param object rootObject
     * @return parsed value
     */
    public static String getStringValue(String expression, Object object) {
        return "" + SPEL_EXPRESSION_PARSER.parseExpression(expression).getValue(object); // use string concatenation to avoid ClassCastException
    }

    /**
     * Retrieve the value of the given expression
     *
     * @param expression SPel expression
     * @return parsed value
     */
    public static String getStringValue(String expression) {
        return SPEL_EXPRESSION_PARSER.parseExpression(expression).getValue().toString();
    }

}
