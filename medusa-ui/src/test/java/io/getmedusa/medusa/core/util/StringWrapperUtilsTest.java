package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringWrapperUtilsTest {

    @Test
    void testBasicWrapString() {
        Assertions.assertEquals("'a'", WrapperUtils.wrapObject("a"));
    }

    @Test
    void testBasicNoWrapInteger() {
        Assertions.assertEquals(1, WrapperUtils.wrapObject(1));
    }

    @Test
    void testBasicNoWrapAlreadyWrapped() {
        Assertions.assertEquals("'a'", WrapperUtils.wrapObject("'a'"));
    }

    @Test
    void testBasicNoWrapAlreadyWrappedDoubleQuotes() {
        Assertions.assertEquals("\"a\"", WrapperUtils.wrapObject("\"a\""));
    }

    @Test
    void testBasicWrapWithSingleQuotePresent() {
        Assertions.assertEquals("'it\\'s a new day, m\\'lord'", WrapperUtils.wrapObject("it's a new day, m'lord"));
    }

}
