package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ValueTagTest {

    private static final ValueTag TAG = new ValueTag();
    public static final String HTML = "<p>Counter: [$counter-value]</p>";
    public static final String HTML_VALUE_AS_ATTRIBUTE = "<input type=\"text\" value=\"[$counter-value]\" />";
    public static final String HTML_WRAPPED_IN_TAG = "<p>[$counter-value]</p>";
    public static final String HTML_TITLE = "<title>Welcome to Medusa :: [$counter-value] :: More title</title>";
    public static final String HTML_WITH_SPACES = "<p>Counter: [$ counter-value]</p>";

    @Test
    void testWithValueAsAttribute() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", 2654);
        String html = TAG.injectWithVariables(new InjectionResult(HTML_VALUE_AS_ATTRIBUTE), variables).getHtml();
        System.out.println(html);
        Assertions.assertTrue(html.contains("2654"));
        Assertions.assertTrue(html.contains("from-value=\"counter-value\""));
        Assertions.assertFalse(html.contains("<span"));
    }

    @Test
    void testWithValueStandard() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", 123);
        String html = TAG.injectWithVariables(new InjectionResult(HTML), variables).getHtml();
        System.out.println(html);
        Assertions.assertTrue(html.contains("123"));
        Assertions.assertTrue(html.contains("from-value=\"counter-value\""));
        Assertions.assertTrue(html.contains("<span"));
    }

    @Test
    void testWrappedInTag() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", 98798);
        String html = TAG.injectWithVariables(new InjectionResult(HTML_WRAPPED_IN_TAG), variables).getHtml();
        System.out.println(html);
        Assertions.assertTrue(html.contains(">98798<"));
        Assertions.assertTrue(html.contains("from-value=\"counter-value\""));
        Assertions.assertFalse(html.contains("span"));
    }

    @Test
    void testWrappedInTitle() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", 4564);
        String html = TAG.injectWithVariables(new InjectionResult(HTML_TITLE), variables).getHtml();
        System.out.println(html);
        Assertions.assertEquals("<title>Welcome to Medusa :: 4564 :: More title</title>", html);
    }

    @Test
    void testWithValueWithSpaces() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("counter-value", 321);
        String html = TAG.injectWithVariables(new InjectionResult(HTML_WITH_SPACES), variables).getHtml();
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
            String html = TAG.injectWithVariables(new InjectionResult(HTML_WRAPPED_IN_TAG), variables).getHtml();
            System.out.println(html);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(e.getMessage().contains("counter-value"));
        }
    }

    class ComplexObject {

        private final String exampleValue;

        public ComplexObject(String exampleValue) {
            this.exampleValue = exampleValue;
        }

        public String getExampleValue() {
            return exampleValue;
        }
    }

}
