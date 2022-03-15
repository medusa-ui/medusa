package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IterationElementIndexingIntegrationTest extends AbstractSeleniumTest {

    static final List<String> RESULTS = List.of("one", "two", "three");
    static final List<String> RESULTS_AFTER_DOM_CHANGES = List.of("one", "two", "three", "four");

    @Test
    @DisplayName("Sanity check for plain old direct indexing")
    void sanityCheckIndexing() {
        goTo("/test/bug/iteration-indexing");

        String directA = getTextById("direct-a");
        String directB = getTextById("direct-b");
        String directC = getTextById("direct-c");

        Assertions.assertEquals("one", directA);
        Assertions.assertEquals("two", directB);
        Assertions.assertEquals("three", directC);
    }

    @Test
    @DisplayName("Check for indexing by iteration element")
    void indexByIterationElement() {
        goTo("/test/bug/iteration-indexing");

        List<String> strs = getAllTextByClass("indirect-strings");
        Assertions.assertEquals( RESULTS, strs);
    }

    @Test
    @DisplayName("Check for indexing by iteration element's property")
    void indexByIterationElementProperty() {
        goTo("/test/bug/iteration-indexing");

        List<String> objs = getAllTextByClass("indirect-names");
        Assertions.assertEquals( RESULTS, objs);
    }

    @Test
    @DisplayName("Check for indexing after DOMChanges")
    void indexAfterDOMChanges() {
        goTo("/test/bug/iteration-indexing");

        clickById("btn_changes");

        List<String> strs = getAllTextByClass("indirect-strings");
        Assertions.assertEquals( RESULTS_AFTER_DOM_CHANGES, strs);

        List<String> objs = getAllTextByClass("indirect-names");
        Assertions.assertEquals( RESULTS_AFTER_DOM_CHANGES, objs);
    }

}