package io.getmedusa.medusa.core.rewrite;

import io.getmedusa.medusa.core.injector.HashGenerationService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GenerateHashesForDOMTest {

    private HashGenerationService hashGenerationService = new HashGenerationService();

    @Test
    void testContentHash() {
        Document document = Jsoup.parse(SAMPLE_DOM);
        hashGenerationService.recursivelyAddPath(document,true);

        Element span = document.getAllElements().stream().filter(e->e.tagName().equals("span")).findFirst().get();
        Element body = document.getAllElements().stream().filter(e->e.tagName().equals("body")).findFirst().get();
        Assertions.assertNotNull(span);
        Assertions.assertNotNull(body);

        Assertions.assertTrue(span.hasAttr("c"));
        String spanContent = span.attributes().get("c");
        Assertions.assertNotNull(spanContent);

        Assertions.assertFalse(body.hasAttr("c"));
    }

    @Test
    void testFindPathForEachNode() {
        Document document = Jsoup.parse(SAMPLE_DOM);
        hashGenerationService.recursivelyAddPath(document,false);

        Element span = document.getAllElements().stream().filter(e->e.tagName().equals("span")).findFirst().get();
        Assertions.assertNotNull(span);

        String path = span.attributes().get("p");
        Assertions.assertNotNull(path);

        Assertions.assertEquals("html>body>p[1]>span", path);
    }

    @Test
    void testHash() {
        String a = "A";
        String b = "B";

        String hashA = hashGenerationService.hash(a);
        String hashB = hashGenerationService.hash(b);
        String hashC = hashGenerationService.hash("A");

        Assertions.assertNotEquals(a, hashA);
        Assertions.assertNotEquals(b, hashB);
        Assertions.assertNotEquals(hashA, hashB);
        Assertions.assertEquals(hashA, hashC);
    }

    public static final String SAMPLE_DOM = """
            <!DOCTYPE html>
            <html>
                <body>
                
                <p>
                This paragraph
                contains a lot of lines
                in the source code,
                but the browser\s
                ignores it.
                </p>
                
                <table>
                  <tr>
                    <th>Company</th>
                    <th>Contact</th>
                    <th>Country</th>
                  </tr>
                  <tr>
                    <td>Alfreds Futterkiste</td>
                    <td>Maria Anders</td>
                    <td>Germany</td>
                  </tr>
                  <tr>
                    <td>Centro comercial Moctezuma</td>
                    <td>Francisco Chang</td>
                    <td>Mexico</td>
                  </tr>
                </table>
                
                <p>
                The number of lines in a <span>lot</span> paragraph depends on the size of the browser window. If you resize the browser window, the number of lines in this paragraph will change.
                </p>
                
                </body>
                </html>""";

}
