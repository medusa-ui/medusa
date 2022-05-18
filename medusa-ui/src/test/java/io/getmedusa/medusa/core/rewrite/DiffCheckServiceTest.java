package io.getmedusa.medusa.core.rewrite;

import io.getmedusa.medusa.core.injector.JSReadyDiff;
import io.getmedusa.medusa.core.injector.DiffCheckService;
import io.getmedusa.medusa.core.injector.DiffType;
import io.getmedusa.medusa.core.injector.HashGenerationService;
import io.getmedusa.medusa.core.registry.ActiveDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DiffCheckServiceTest {

    private final DiffCheckService diffCheckService = new DiffCheckService();
    private final HashGenerationService hashGenerationService = new HashGenerationService();

    @Test
    void testAdditionEnd() {
        final List<JSReadyDiff> diffs = getDiffs("""
                <p>A</p>
                <p>B</p>
        """, """
                <p>A</p>
                <p>B</p>
                <p>ADDITION AT THE END</p>
        """);
        Assertions.assertNotNull(diffs);
        Assertions.assertEquals(1, diffs.size());

        JSReadyDiff diff = diffs.get(0);

        Assertions.assertEquals("/html[1]/body[1]/p[3]", diff.getXpath());
        Assertions.assertEquals(DiffType.ADDITION, diff.getType());
        Assertions.assertTrue(diff.getContent().contains("<p"));
        Assertions.assertTrue(diff.getContent().contains(">ADDITION AT THE END</p>"), "Should match '>ADDITION AT THE END</p>'");
    }

    @Test
    void testAdditionStart() {
        final List<JSReadyDiff> diffs = getDiffs("""
                <p>A</p>
                <p>A</p>
        """, """
                <p>ADDITION AT THE START</p>
                <p>A</p>
                <p>A</p>
        """);
        Assertions.assertNotNull(diffs);
        Assertions.assertEquals(2, diffs.size());

        JSReadyDiff firstDiff = diffs.get(0);
        Assertions.assertEquals("/html[1]/body[1]/p[1]", firstDiff.getXpath());
        Assertions.assertEquals(DiffType.EDIT, firstDiff.getType());
        Assertions.assertEquals("ADDITION AT THE START", firstDiff.getContent());

        JSReadyDiff secondDiff = diffs.get(1);
        Assertions.assertEquals("/html[1]/body[1]/p[3]", secondDiff.getXpath());
        Assertions.assertEquals(DiffType.ADDITION, secondDiff.getType());
        Assertions.assertTrue(secondDiff.getContent().contains(">A</p>"), "Should match '>A</p>'");
    }

    @Test
    void testEdit() {
        final List<JSReadyDiff> diffs = getDiffs("""
                <body>
                <p><span>B</span></p>
                </body>
        """, """
                <body>
                <p><span>EDITED</span></p>
                </body>
        """);
        Assertions.assertNotNull(diffs);
        Assertions.assertEquals(1, diffs.size());
    }

    @Test
    void testAdditionBetween() {
        final List<JSReadyDiff> diffs = getDiffs("""
            <html>
                <body>
                    <p>A</p>
                    <p>A</p>
                    <p>A</p>
                    <p>A</p>
                </body>
            </html>
        """, """
            <html>
                <body>
                    <p>A</p>
                    <p>A</p>
                    <p>BETWEEN</p>
                    <p>A</p>
                    <p>A</p>
                </body>
            </html>
        """);
        Assertions.assertNotNull(diffs);
        Assertions.assertEquals(1, diffs.size());
    }
    @Test
    void testAdditionInside() {
        final List<JSReadyDiff> diffs = getDiffs("""
                <p>A</p>
                <p></p>
                <p>C</p>
        """, """
                <p>A</p>
                <p><span>B</span></p>
                <p>C</p>
        """);
        Assertions.assertNotNull(diffs);
        Assertions.assertEquals(1, diffs.size());
    }

    @Test
    void testRemovalStart() {
        final List<JSReadyDiff> diffs = getDiffs("""
                <p>A</p>
                <p>B</p>
                <p>C</p>
        """, """
                <p>B</p>
                <p>C</p>
        """);
        Assertions.assertNotNull(diffs);
        Assertions.assertEquals(1, diffs.size());
    }

    @Test
    void testRemovalEnd() {
        final List<JSReadyDiff> diffs = getDiffs("""
                <p>A</p>
                <p>B</p>
                <p>C</p>
        """, """
                <p>A</p>
                <p>B</p>
        """);
        Assertions.assertNotNull(diffs);
        Assertions.assertEquals(1, diffs.size());
    }

    @Test
    void testRemovalInside() {
        final List<JSReadyDiff> diffs = getDiffs("""
                <p>A</p>
                <p><span>B</span></p>
                <p>C</p>
        """, """
                <p>A</p>
                <p></p>
                <p>C</p>
        """);
        Assertions.assertNotNull(diffs);
        Assertions.assertEquals(1, diffs.size());
    }

    private ActiveDocument buildActiveDocument(String html) {
        return new ActiveDocument("index.html", "/", Jsoup.parse(html), null);
    }

    private List<JSReadyDiff> getDiffs(String a, String b) {
        ActiveDocument previous = buildActiveDocument(a);
        Document next = Jsoup.parse(b);
        //hashGenerationService.recursivelyAddPath(previous.getDocument());
        //hashGenerationService.recursivelyAddPath(next);
        return diffCheckService.diffCheckDocuments(previous, next);
    }

}
