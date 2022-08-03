package io.getmedusa.medusa.core.session;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.util.JSONUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class SessionTest {

    @Test
    void testSessionIsSerializable() {
        final Session session = new Session();
        Assertions.assertNotNull(JSONUtils.serialize(session));

        session.setLastParameters(List.of(new Attribute("name123", "value123")));
        final String serialized = JSONUtils.serialize(session);
        Assertions.assertTrue(serialized.contains("lastParameters"));
        Assertions.assertTrue(serialized.contains("name123"));
        Assertions.assertTrue(serialized.contains("value123"));
    }

    @Test
    void testMergeAttributes() {
        final Session session = new Session();
        session.setLastParameters(List.of(new Attribute("x", 1), new Attribute("y", 2)));

        final Session newSession = session.merge(List.of(new Attribute("x", 3)));
        final Map<String, Object> lastParameters = newSession.toLastParameterMap();
        System.out.println(lastParameters);

        Assertions.assertEquals(3, lastParameters.get("x"));
        Assertions.assertEquals(2, lastParameters.get("y"));
    }

}
