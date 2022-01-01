package io.getmedusa.medusa.core.injector.tag;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class SelectedTagTest extends AbstractTest {

    public static final String HTML = """
            <select m:selected="selected-option">
              <option value="volvo">Volvo</option>
              <option value="saab">Saab</option>
              <option value="vw">VW</option>
              <option value="audi" selected>Audi</option>
            </select>
        """;

    @Test
    void testSingleSelected() {
        Document doc = inject(HTML, Map.of("selected-option", "saab"));
        System.out.println(doc.html());

        Elements selectedElements = doc.select("option[selected]");
        Assertions.assertEquals(1, selectedElements.size());
        Assertions.assertEquals("Saab", selectedElements.first().text());
    }

    @Test
    void testNoneSelected() {
        Document doc = inject(HTML, Map.of("selected-option", "x"));
        System.out.println(doc.html());

        Elements selectedElements = doc.getElementsByAttributeValue("selected", "selected");
        Assertions.assertEquals(0, selectedElements.size());
    }
}
