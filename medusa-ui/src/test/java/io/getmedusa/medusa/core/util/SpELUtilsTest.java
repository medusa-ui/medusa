package io.getmedusa.medusa.core.util;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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


}
