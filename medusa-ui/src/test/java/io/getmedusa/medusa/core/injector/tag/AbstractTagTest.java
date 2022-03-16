package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.registry.EachValueRegistry;
import io.getmedusa.medusa.core.util.TestRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class AbstractTagTest {

    private ValueTag tag = new ValueTag();

    private final String HTML = """
            <div m-each="str" template-id="t-1889897380" index="0">
                <m:text item="map[str]" />
            </div>
            """;

    @Test
    void testGetPossibleEachValue() {
        List<String> listOfString = List.of("A", "B", "C");
        Map<String, String> map = Map.of(
                listOfString.get(0), "one",
                listOfString.get(1), "two",
                listOfString.get(2), "three");

        Map<String, Object> variables = Map.of(
                "list-of-strings", listOfString,
                "map", map
        );
        Document document = Jsoup.parse(HTML, Parser.xmlParser());

        Elements elements = document.getElementsByTag("m:text");

        final TestRequest request = new TestRequest();
        EachValueRegistry.getInstance().add(request, "str", 0, listOfString.get(0));
        EachValueRegistry.getInstance().add(request, "str", 1, listOfString.get(1));
        EachValueRegistry.getInstance().add(request, "str", 2, listOfString.get(2));

        final Object possibleEachValue = tag.getPossibleEachValue(elements.get(0), "map[str]", request, variables);
        Assertions.assertEquals("one", possibleEachValue);
    }

}
