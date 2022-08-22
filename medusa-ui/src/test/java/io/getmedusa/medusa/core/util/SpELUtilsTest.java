package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.Map;

class SpELUtilsTest {

    public static final Session SESSION = buildSession();

    private static Session buildSession() {
        Session session = new Session();
        session.setLastParameters(List.of(new Attribute("xyz", 123)));
        return session;
    }

    @Test
    void testSimple() {
        Assertions.assertEquals("hello-world", SpELUtils.parseExpression("hello-world", SESSION));
        Assertions.assertEquals("hello-world", SpELUtils.parseExpression("${'hello' + '-world'}", SESSION));
    }

    @Test
    void testSessionVariables() {
        Assertions.assertEquals("hello-123", SpELUtils.parseExpression("${'hello-' + xyz}", SESSION));
    }

    private static final SpelExpressionParser SPEL_EXPRESSION_PARSER = new SpelExpressionParser();

    @Test
    void testSampleFormAsMap() {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.addPropertyAccessor(new MapAccessor());

        Map<String, Object> result = SPEL_EXPRESSION_PARSER
                .parseExpression("{\"firstName\": \"John\", \"lastName\": \"Doe\"}")
                .getValue(evaluationContext, Map.class);
        System.out.println(result);
        Assertions.assertEquals("John", result.get("firstName"));
    }
}
