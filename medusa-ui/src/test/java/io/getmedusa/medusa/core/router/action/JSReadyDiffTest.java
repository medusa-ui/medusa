package io.getmedusa.medusa.core.router.action;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JSReadyDiffTest {

    @Test
    void testXPathPrevious_Numerical() {
        Assertions.assertEquals("/table[1]/tr[1]", JSReadyDiff.determinePreviousNode("/table[1]/tr[2]"));
        Assertions.assertEquals("/table[1]/tr[2]", JSReadyDiff.determinePreviousNode("/table[1]/tr[3]"));
        Assertions.assertEquals("/table[1]/tr[19]", JSReadyDiff.determinePreviousNode("/table[1]/tr[20]"));
    }

    @Test
    void testXPathPrevious_First() {
        Assertions.assertEquals("/table[1]/::first", JSReadyDiff.determinePreviousNode("/table[1]/tr[1]"));
    }
}
