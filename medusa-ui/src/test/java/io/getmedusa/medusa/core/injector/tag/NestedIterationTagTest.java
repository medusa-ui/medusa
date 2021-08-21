package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.util.NestedForEachParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class NestedIterationTagTest {

    private static final IterationTag TAG = new IterationTag();
    private static final NestedForEachParser PARSER = new NestedForEachParser();
    public static final String HTML =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
                "[$foreach $list-of-values]\n" +
                    "0\n" +
                    "[$foreach $list-of-values]1[$end for]\n" +
                    "2\n" +
                "[$end for]\n" +
            "</body>\n" +
            "</html>";

    public static final String HTML_DEEPER =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
            "[$foreach $list-of-values]\n" +
                "0\n" +
                "[$foreach $list-of-values]\n" +
                    "1\n" +
                    "[$foreach $list-of-values]\n" +
                        "2\n" +
                    "[$end for]\n" +
                    "3\n" +
                "[$end for]\n" +
                "4\n" +
            "[$end for]\n" +
            "</body>\n" +
            "</html>";

    @Test
    void testDepthParser() {
        Set<ForEachElement> elements = PARSER.buildDepthElements(HTML);
        Assertions.assertEquals(2, elements.size());

        int countWithParent = 0;

        for (ForEachElement element : elements) {
            if(element.parent != null) countWithParent++;
            Assertions.assertEquals("list-of-values", element.condition);
        }

        Assertions.assertEquals(1, countWithParent);
    }

    @Test
    void testDepthParserDeeper() {
        Set<ForEachElement> elements = PARSER.buildDepthElements(HTML_DEEPER);
        Assertions.assertEquals(3, elements.size());

        int countWithParent = 0;

        for (ForEachElement element : elements) {
            if(element.parent != null) countWithParent++;
            Assertions.assertEquals("list-of-values", element.condition);
        }

        Assertions.assertEquals(2, countWithParent);
    }

    @Test
    void testSingleElementList() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", Collections.singletonList(1));
        InjectionResult result = TAG.injectWithVariables(new InjectionResult(HTML), variables);
        System.out.println();
        System.out.println("RESULT");
        System.out.println("/*****/");
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
        Assertions.assertEquals(2, countOccurrences(result.getHtml()));
    }

    @Test
    void testMultipleLayersSingleElementList() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", Collections.singletonList(1));
        InjectionResult result = TAG.injectWithVariables(new InjectionResult(HTML_DEEPER), variables);
        System.out.println(result.getHtml());
        Assertions.assertFalse(result.getHtml().contains("$foreach"));
        Assertions.assertTrue(result.getHtml().contains("<template") && result.getHtml().contains("</template>"));
        Assertions.assertEquals(3, countOccurrences(result.getHtml()));
    }

    long countOccurrences(String block) {
        return Arrays.stream(block.split("<template")).count()-1;
    }
}
