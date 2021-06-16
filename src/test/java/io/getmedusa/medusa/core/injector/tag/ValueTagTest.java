package io.getmedusa.medusa.core.injector.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

public class ValueTagTest {

    private static final ValueTag TAG = new ValueTag();
    public static final String HTML =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
            "<p>Counter: [$counter-value]</p>\n" +
            "</body>\n" +
            "</html>";

    @Test
    void testMatcher() {
        Matcher matcher = TAG.buildMatcher(HTML);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("[$counter-value]", matcher.group(0));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    void testReplacement() {
        final String fullMatch = "[$counter-value]";
        String replacedHTML = TAG.substitutionLogic(fullMatch, "counter-value");
        Assertions.assertEquals("<span id=\"some-id\" from-value=\'counter-value\'>0</span>", replacedHTML);
    }

    @Test
    void testFullToTag() {
        final String fullMatch = "[$counter-value]";
        final String tagContent = "counter-value";

        Assertions.assertEquals(tagContent, TAG.fullMatchToTagContent(fullMatch));
    }

}
