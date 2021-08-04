package io.getmedusa.medusa.core.injector.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

public class OnEnterTagTest {

    private static final OnEnterTag TAG = new OnEnterTag();
    public static final String HTML =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
            "<input m-onenter=\"search(this.value, 3,this.type,this.name)\" type=\"text\" name=\"term\" autocomplete=\"off\" />\n" +
            "</body>\n" +
            "</html>";

    @Test
    void testMatcher() {
        Matcher matcher = TAG.buildMatcher(HTML);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("m-onenter=\"search(this.value, 3,this.type,this.name)\"", matcher.group(0));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    void testReplacement() {
        final String fullMatch = "m-onenter=\"search(this.value, 3,this.type,this.name)\"";
        final String tagContent = "search(this.value, 3,this.type,this.name)";

        String replacedHTML = TAG.substitutionLogic(fullMatch, tagContent);
        Assertions.assertEquals("onkeydown=\"_M.preventDefault(this)\" onkeyup=\"_M.onEnter(this, 'search(this.value, 3,this.type,this.name)')\"", replacedHTML);
    }

    @Test
    void testReplacementSingleQuotedAction() {
        final String fullMatch = "m-onenter='search(this.value, 3, this.type, this.name)'";
        final String tagContent = "search(this.value, 3, this.type, this.name)";

        String replacedHTML = TAG.substitutionLogic(fullMatch, tagContent);
        Assertions.assertEquals("onkeydown=\"_M.preventDefault(this)\" onkeyup=\"_M.onEnter(this, 'search(this.value, 3, this.type, this.name)')\"", replacedHTML);
    }

    @Test
    void testFullToTag() {
        final String fullMatch = "m-onenter=\"search(this.value, 3,this.type,this.name)\"";
        final String tagContent = "search(this.value, 3,this.type,this.name)";

        Assertions.assertEquals(tagContent, TAG.fullMatchToTagContent(fullMatch));
    }

}
