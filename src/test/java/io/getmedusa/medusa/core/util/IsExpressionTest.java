package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IsExpressionTest {

    @Test
    void isExpression() {
        String expression1 = "5 > 0";
        String expression2 = "!(5 > 0)";
        String expression3 = "Hello world";
        String expression4 = "'Hello' + ' world'";
        String expression5 = "Perseus";

        Assertions.assertTrue(SpelExpressionParserHelper.isExpression(expression1));
        Assertions.assertTrue(SpelExpressionParserHelper.isExpression(expression2));
        Assertions.assertFalse(SpelExpressionParserHelper.isExpression(expression3));
        Assertions.assertTrue(SpelExpressionParserHelper.isExpression(expression4));
        Assertions.assertFalse(SpelExpressionParserHelper.isExpression(expression5));
    }

}
