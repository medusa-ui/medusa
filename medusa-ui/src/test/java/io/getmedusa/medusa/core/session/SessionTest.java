package io.getmedusa.medusa.core.session;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.util.JSONUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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

}
