package io.getmedusa.medusa.core.session;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.boot.Fragment;
import io.getmedusa.medusa.core.util.JSONUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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

    @Test
    void testLocaleSetting() {
        Session session = new Session();
        session = session.withLocale("en-US");
        Assertions.assertEquals(Locale.US, session.getLocale());

        session = session.withLocale("en-GB");
        Assertions.assertEquals(Locale.UK, session.getLocale());

        session = session.withLocale("it");
        Assertions.assertEquals(Locale.ITALIAN, session.getLocale());
    }

    //this test does the code for import/exports
    //without any, we would expect a brand new session
    //this can be extended with explicit imports from root/exports to root via fragment
    @Test
    void testIsolationImports() {
        Session session = new Session();
        session.setLastRenderedHTML("hello");
        session.setLastParameters(Attribute.$$("test", 123, "test2", 456));

        Assertions.assertEquals(2, session.getLastParameters().size(), "Expected to start w/ 2 items");

        final Session fragmentSessionNoFragment = session.isolateImports(null);

        Assertions.assertEquals("hello",
                fragmentSessionNoFragment.getLastRenderedHTML(),
                "Excepted data like lastRenderedHTML still to be present");

        Assertions.assertEquals(2,
                fragmentSessionNoFragment.getLastParameters().size(),
                "With no fragment, expected list to be same as root session, so 2");

        Fragment fragment = new Fragment();
        fragment.setRef("x");
        fragment.setImports(Arrays.asList("test", "test2 as newValue"));

        final Session fragmentSession = session.isolateImports(fragment);

        Assertions.assertEquals(2, fragmentSession.getLastParameters().size(), "With imports, expected 2 items");
        Assertions.assertTrue(fragmentSession.getLastParameters().stream().anyMatch(a -> "test".equals(a.name())), "Expected one of the attributes to be 'test'");
        Assertions.assertTrue(fragmentSession.getLastParameters().stream().anyMatch(a -> "newValue".equals(a.name())), "Expected one of the attributes to be 'newValue', the alias");


        session.cleanAndIsolateExports(fragment);
        Assertions.assertEquals(2, session.getLastParameters().size(), "Expected to end w/ 2 items");
        Assertions.assertTrue(session.getLastParameters().stream().anyMatch(a -> "test".equals(a.name())), "Expected one of the attributes to be 'test'");
        Assertions.assertTrue(session.getLastParameters().stream().anyMatch(a -> "test2".equals(a.name())), "Expected one of the attributes to be 'test2'");
    }

    //session.cleanAndIsolateExports(fragment);


}
