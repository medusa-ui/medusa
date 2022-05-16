package io.getmedusa.medusa.core.rewrite;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class GenerateHashesForDOMTest {

    protected static final HashFunction HASH_FUNCTION = Hashing.murmur3_32_fixed();

    @Test
    void testContentHash() {
        Document document = Jsoup.parse(SAMPLE_DOM);
        recursivelyAddPath(document.children(), "",true);
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
        recursivelyAddPath(document.children(), "",false);
        Element span = document.getAllElements().stream().filter(e->e.tagName().equals("span")).findFirst().get();
        Assertions.assertNotNull(span);

        String path = span.attributes().get("p");
        Assertions.assertNotNull(path);

        Assertions.assertEquals("html>body>p[1]>span", path);

        System.out.println(path);
    }

    private void recursivelyAddPath(Elements elements, String context, boolean applyHash) {
        for(Element element : elements) {
            StringBuilder hashBasis = new StringBuilder(context);
            if(!context.isBlank()) {
                hashBasis.append(">");
            }
            hashBasis.append(element.tagName());

            List<Element> siblings = findSiblings(element);

            if(siblings.size() > 1) {
                hashBasis.append("[");
                hashBasis.append(siblings.indexOf(element));
                hashBasis.append("]");
            }

            final String newContext = hashBasis.toString();

            applyPathHash(applyHash, element, newContext);
            applyContentHash(applyHash, element);

            if(element.childrenSize() > 0) {
                recursivelyAddPath(element.children(), newContext, applyHash);
            }
        }
    }

    private void applyPathHash(boolean applyHash, Element element, String newContext) {
        element.attr("p", applyHash ? hash(newContext) : newContext);
    }

    private void applyContentHash(boolean applyHash, final Element element) {
        if(applyHash) {
            String directContent = element.ownText(); //TODO remove child nodes first
            if(!directContent.isBlank()) {

                if(element.childrenSize() > 0) {
                    Element clone = element.clone();
                    clone.children().remove();
                    directContent = clone.ownText();
                }

                element.attr("c", hash(directContent));
            }
        }
    }

    private List<Element> findSiblings(Element element) {
        if(element.parent() == null) return new ArrayList<>();
        return element.parent().children().stream().filter(s -> s.tagName().equals(element.tagName())).toList();
    }

    private String hash(String raw) {
        return HASH_FUNCTION.hashString(raw, StandardCharsets.UTF_8).toString();
    }

    @Test
    void testHash() {
        String a = "A";
        String b = "B";

        String hashA = hash(a);
        String hashB = hash(b);
        String hashC = hash("A");

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
