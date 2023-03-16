package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.session.Session;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELUtils {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public static String parseExpression(String expression, Session session) {
        if(expression.startsWith("${") && expression.endsWith("}")) {
            expression = expression.substring(2, expression.length()-1);
        } else {
            return expression;
        }

        StandardEvaluationContext context = new StandardEvaluationContext(session.toLastParameterMap());
        context.addPropertyAccessor(new MapAccessor());

        return PARSER.parseExpression(expression).getValue(context, String.class);
    }

}
