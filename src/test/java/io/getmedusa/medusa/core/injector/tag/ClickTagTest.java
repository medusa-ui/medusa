package io.getmedusa.medusa.core.injector.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

public class ClickTagTest {

    private static final ClickTag TAG = new ClickTag();
    public static final String HTML =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
            "<button m-click=\"increaseCounter('2')\">Increase counter</button>\n" +
            "</body>\n" +
            "</html>";

    @Test
    void testMatcher() {
        Matcher matcher = TAG.buildMatcher(HTML);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("m-click=\"increaseCounter('2')\"", matcher.group(0));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    void testReplacement() {
        final String fullMatch = "m-click=\"increaseCounter('2')\"";
        final String tagContent = "increaseCounter('2')";

        String replacedHTML = TAG.substitutionLogic(fullMatch, tagContent);
        Assertions.assertEquals("onclick=\"sendEvent(this, 'increaseCounter(\\'2\\')')\"", replacedHTML);
    }

    @Test
    void testReplacementSingleQuotedAction() {
        final String fullMatch = "m-click='increaseCounter(\"2\")'";
        final String tagContent = "increaseCounter('2')";

        String replacedHTML = TAG.substitutionLogic(fullMatch, tagContent);
        Assertions.assertEquals("onclick=\"sendEvent(this, 'increaseCounter(\\'2\\')')\"", replacedHTML);
    }

    @Test
    void testFullToTag() {
        final String fullMatch = "m-click=\"increaseCounter(2)\"";
        final String tagContent = "increaseCounter(2)";

        Assertions.assertEquals(tagContent, TAG.fullMatchToTagContent(fullMatch));
    }

}
