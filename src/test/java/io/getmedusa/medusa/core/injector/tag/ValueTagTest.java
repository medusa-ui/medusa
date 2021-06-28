package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

class ValueTagTest {

    private static final ValueTag TAG = new ValueTag();
    public static final String HTML = "<p>Counter: [$counter-value]</p>";
    public static final String HTML_VALUE_AS_ATTRIBUTE = "<input type=\"text\" value=\"[$counter-value]\" />";

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

}
