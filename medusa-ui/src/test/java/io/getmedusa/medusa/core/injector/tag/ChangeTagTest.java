package io.getmedusa.medusa.core.injector.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

public class ChangeTagTest {

    private static final ChangeTag TAG = new ChangeTag();
    public static final String HTML =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
            "<form><input m-change=\"search()\" name=\"term\" /></form>\n" +
            "</body>\n" +
            "</html>";

    @Test
    void testMatcher() {
        Matcher matcher = TAG.buildMatcher(HTML);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("m-change=\"search()\"", matcher.group(0));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    void testReplacement() {
        final String fullMatch = "m-change=\"search()\"";
        final String tagContent = "search()";

        String replacedHTML = TAG.substitutionLogic(fullMatch, tagContent);
        Assertions.assertEquals("oninput=\"_M.sendEvent(this, 'search()')\"", replacedHTML);
    }

    @Test
    void testFullToTag() {
        final String fullMatch = "m-change=\"search()\"";
        final String tagContent = "search()";

        Assertions.assertEquals(tagContent, TAG.fullMatchToTagContent(fullMatch));
    }

}
