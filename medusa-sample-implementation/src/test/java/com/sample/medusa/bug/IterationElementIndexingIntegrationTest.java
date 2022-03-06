package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IterationElementIndexingIntegrationTest extends AbstractSeleniumTest {

    static final List<String> RESULTS = List.of("one", "two", "three");

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
        Assertions.assertEquals(strs, RESULTS);
    }

    @Test
    @DisplayName("Check for indexing by iteration element's property")
    void indexByIterationElementProperty() {
        goTo("/test/bug/iteration-indexing");

        List<String> objs = getAllTextByClass("indirect-names");
        Assertions.assertEquals(objs, RESULTS);
    }

}
