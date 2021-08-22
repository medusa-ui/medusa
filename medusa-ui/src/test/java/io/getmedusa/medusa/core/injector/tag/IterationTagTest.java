package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.util.EachParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class IterationTagTest {

    private static final IterationTag TAG = new IterationTag();
    private static final EachParser PARSER = new EachParser();

    public static final String HTML =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
            "[$foreach $list-of-values]<p>Medusa</p>[$end for]" +
            "</body>\n" +
            "</html>";

    public static final String HTML_W_ELEMENT =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
            "[$foreach $list-of-values]<p>Bought [$each]</p>[$end for]" +
            "</body>\n" +
            "</html>";

    public static final String HTML_MULTIPLE_FORS =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
            "[$foreach $list-of-values]<p>Medusa 1</p>[$end for] x " +
            "[$foreach $list-of-values]<p>Medusa 2</p>[$end for]" +
            "</body>\n" +
            "</html>";

    //TODO: properties for each

    @Test
    void testDepthParser() {
        List<ForEachElement> elements = PARSER.buildDepthElements(HTML);
        Assertions.assertEquals(1, elements.size());

        ForEachElement element = elements.iterator().next();
        Assertions.assertNull(element.getParent());
        Assertions.assertEquals(0, element.getDepth());
        Assertions.assertEquals("list-of-values", element.condition);
    }

    @Test
    void testDepthParserMultiple() {
        List<ForEachElement> elements = PARSER.buildDepthElements(HTML_MULTIPLE_FORS);
        Assertions.assertEquals(2, elements.size());

        ForEachElement element = elements.iterator().next();
        Assertions.assertNull(element.getParent());
        Assertions.assertEquals(0, element.getDepth());
        Assertions.assertEquals("list-of-values", element.condition);

        element = elements.iterator().next();
        Assertions.assertNull(element.getParent());
        Assertions.assertEquals(0, element.getDepth());
        Assertions.assertEquals("list-of-values", element.condition);
    }

    @Test
    void testEmptyList() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", new ArrayList<>());
        InjectionResult result = TAG.injectWithVariables(new InjectionResult(HTML), variables);
        System.out.println(result.getHtml());
        Assertions.assertFalse(result.getHtml().contains("$foreach"));
        Assertions.assertTrue(result.getHtml().contains("<template") && result.getHtml().contains("</template>"));
        Assertions.assertEquals(1, countOccurrences(result.getHtml()));
    }

    @Test
    void testSingleElementList() {
        System.out.println(HTML);
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", Collections.singletonList(1));
        InjectionResult result = TAG.injectWithVariables(new InjectionResult(HTML), variables);
        System.out.println(result.getHtml());
        Assertions.assertFalse(result.getHtml().contains("$foreach"));
        Assertions.assertTrue(result.getHtml().contains("<template") && result.getHtml().contains("</template>"));
        Assertions.assertEquals(2, countOccurrences(result.getHtml()));
    }

    @Test
    void testMultipleElementList() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", Arrays.asList(1,2,3,4,5));
        InjectionResult result = TAG.injectWithVariables(new InjectionResult(HTML), variables);
        System.out.println(result.getHtml());
        Assertions.assertFalse(result.getHtml().contains("$foreach"));
        Assertions.assertTrue(result.getHtml().contains("<template") && result.getHtml().contains("</template>"));
        Assertions.assertEquals(6, countOccurrences(result.getHtml()));

    }

    @Test
    void testForEachWithElement() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", Arrays.asList(1,2,3,4,5));
        InjectionResult result = TAG.injectWithVariables(new InjectionResult(HTML_W_ELEMENT), variables);
        System.out.println(result.getHtml());
        Assertions.assertFalse(result.getHtml().contains("$foreach"));
        Assertions.assertTrue(result.getHtml().contains("<template") && result.getHtml().contains("</template>"));
        for (int i = 1; i <= 5; i++) {
            Assertions.assertTrue(result.getHtml().contains("<p>Bought " + i + "</p>"));
        }
    }

    long countOccurrences(String block) {
        return Arrays.stream(block.split("<p>Medusa</p>")).count()-1;
    }

}
