package io.getmedusa.medusa.core.injector.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ValueTagTest extends AbstractTest {

    public static final String HTML = "<p>Counter: <m:text item=\"counter-value\" /></p>";
    public static final String HTML_VALUE_AS_ATTRIBUTE = "<input type=\"text\" m:value=\"counter-value\" />";
    public static final String HTML_VALUE_AS_COMPLEX_ATTRIBUTE = "<input type=\"text\" m:value=\"obj.exampleValue\" />";
    public static final String HTML_WRAPPED_IN_TAG = "<p><m:text item=\"counter-value\" /></p>";
    public static final String HTML_WITH_SPACES = "<p>Counter: <m:text item=\" counter-value   \" /></p>";
    public static final String HTML_WITH_COMPLEX_OBJECT = "<p>Counter: <m:text item=\"obj.exampleValue\" /></p>";

    //public static final String HTML_ARRAY = "<p>Counter: <m:text item=\"array[0]\" /></p>";
    //public static final String HTML_MAP = "<p>Counter: <m:text item=\"map['x']\" /></p>";
    //public static final String HTML_MAP_W_ITEM = "<p>Counter: <m:text item=\"map[obj.exampleValue]\" /></p>";

    @Test
    void testWithValueStandard() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", 123);
        String html = inject(HTML, variables).html();
        System.out.println(html);
        Assertions.assertTrue(html.contains("123"));
        Assertions.assertTrue(html.contains("from-value=\"counter-value\""));
        Assertions.assertTrue(html.contains("<span"));
    }

    @Test
    void testWithValueComplex() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("obj", new ComplexObject("123"));
        String html = inject(HTML_WITH_COMPLEX_OBJECT, variables).html();
        System.out.println(html);
        Assertions.assertTrue(html.contains("123"));
        Assertions.assertTrue(html.contains("from-value=\"obj.exampleValue\""));
        Assertions.assertTrue(html.contains("<span"));
    }

    @Test
    void testWithValueAsAttribute() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", 2654);
        String html = inject(HTML_VALUE_AS_ATTRIBUTE, variables).html();
        System.out.println(html);
        Assertions.assertTrue(html.contains("2654"));
        Assertions.assertTrue(html.contains("from-value=\"counter-value\""));
        Assertions.assertFalse(html.contains("<span"));
        Assertions.assertFalse(html.contains("m:value"));
    }

    @Test
    void testWithValueAsComplexAttribute() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("obj", new ComplexObject("2654"));
        String html = inject(HTML_VALUE_AS_COMPLEX_ATTRIBUTE, variables).html();
        System.out.println(html);
        Assertions.assertTrue(html.contains("2654"));
        Assertions.assertTrue(html.contains("from-value=\"obj.exampleValue\""));
        Assertions.assertFalse(html.contains("<span"));
        Assertions.assertFalse(html.contains("m:value"));
    }

    @Test
    void testWrappedInTag() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", 98798);
        String html = inject(HTML_WRAPPED_IN_TAG, variables).html();
        System.out.println(html);
        Assertions.assertTrue(html.contains(">98798<"));
        Assertions.assertTrue(html.contains("from-value=\"counter-value\""));
    }

    @Test
    void testWithValueWithSpaces() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", 321);
        String html = inject(HTML_WITH_SPACES, variables).html();
        System.out.println(html);
        Assertions.assertTrue(html.contains("321"));
        Assertions.assertTrue(html.contains("from-value=\"counter-value\""));
        Assertions.assertTrue(html.contains("<span"));
    }

    @Test
    void testObjectRenderingCausesError() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", new ComplexObject("xyz"));

        try {
            String html = inject(HTML_WRAPPED_IN_TAG, variables).html();
            System.out.println(html);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(e.getMessage().contains("counter-value"));
        }
    }

}
