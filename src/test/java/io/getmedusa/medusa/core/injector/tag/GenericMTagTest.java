package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

class GenericMTagTest {

    private static final GenericMTag TAG = new GenericMTag();
    public static final String HTML_WITHOUT_CLASS = "<span m-hide=\"$three-items\" m-disabled=\"$items-bought-size == 0\" m-irrelevant=\"true\" m-ignore>Example</span>";
    public static final String HTML_WITH_CLASS = "<span m-hide=\"$three-items\" class=\"red\" m-disabled=\"$items-bought-size == 0\" m-irrelevant=\"true\" m-ignore>Example</span>";

    public static final String HTML_WITH_STYLE_HIDE = "<span m-hide=\"$three-items\" style=\"color:red;\">Example</span>";

    @Test
    void testParsingWithoutClass() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("three-items", false);
        variables.put("items-bought-size", 1);
        String parsedHTML = TAG.injectWithVariables(new InjectionResult(HTML_WITHOUT_CLASS), variables).getHtml();
        System.out.println(parsedHTML);

        Assertions.assertFalse(parsedHTML.contains("m-disabled"));
        Assertions.assertFalse(parsedHTML.contains("m-hide"));
        Assertions.assertEquals(1, countOccurrences(parsedHTML, "class="));
        Assertions.assertTrue(parsedHTML.contains("class=\"m-"));
        Assertions.assertTrue(parsedHTML.contains("m-irrelevant=\"true\""));
        Assertions.assertFalse(parsedHTML.contains("disabled"));
    }

    @Test
    void testParsingWithClass() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("three-items", true);
        variables.put("items-bought-size", 0);
        String parsedHTML = TAG.injectWithVariables(new InjectionResult(HTML_WITH_CLASS), variables).getHtml();
        System.out.println(parsedHTML);

        Assertions.assertFalse(parsedHTML.contains("m-disabled"));
        Assertions.assertFalse(parsedHTML.contains("m-hide"));
        Assertions.assertTrue(parsedHTML.contains("class=\""));
        Assertions.assertTrue(parsedHTML.contains("m-irrelevant=\"true\""));
        Assertions.assertEquals(1, countOccurrences(parsedHTML, "class="));
        Assertions.assertTrue(parsedHTML.contains("disabled=\"true\""));
        Assertions.assertTrue(parsedHTML.contains("style=\"display:none;\""));
    }

    @Test
    void testParsingWithStyleAndHTML() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("three-items", true);
        String parsedHTML = TAG.injectWithVariables(new InjectionResult(HTML_WITH_STYLE_HIDE), variables).getHtml();
        System.out.println(parsedHTML);

        Assertions.assertEquals(1, countOccurrences(parsedHTML, "style="));
    }


    long countOccurrences(String block, String element) {
        return Arrays.stream(block.split(element)).count()-1;
    }
}
