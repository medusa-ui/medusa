package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.*;
import io.getmedusa.medusa.core.util.EachParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class NestedIterationTagTest {

    private static final Iteration2Tag TAG = new Iteration2Tag();
    private static final EachParser PARSER = new EachParser();
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

    public static final String HTML_MULTIPLE_FORS =
            "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<body>\n" +
                    "[$foreach $list-of-values][$foreach $list-of-values]<p>Medusa 1</p>[$end for][$end for] x " +
                    "[$foreach $list-of-values]<p>Medusa 2</p>[$end for]" +
                    "</body>\n" +
                    "</html>";

    public static final String HTML_BUG =
            "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<body>[$foreach $parent]" +
                        "[$foreach $child]" +
                            "[$parent.each] - [$each]" +
                        "[$end for]" +
                    "[$end for]" +
                    "</body>" +
                    "</html>";

    public static final String HTML_WITH_EACH = "[$foreach $persons] [$foreach $fruits] [$this.each] [$end for] [$end for]";
    public static final String HTML_WITH_EACH_PARENT = "[$foreach $persons] [$foreach $fruits][$foreach $fruits][$parent.parent.each][$end for][$end for] [$end for]";
    protected static final String DUMMY_BLOCK = HTML_WITH_EACH;
    protected static final ForEachElement DUMMY_ELEM = new ForEachElement(DUMMY_BLOCK, DUMMY_BLOCK);

    @Test
    void testDepthParser() {
        List<ForEachElement> elements = PARSER.buildDepthElements(HTML);
        Assertions.assertEquals(2, elements.size());

        int countWithParent = 0;

        for (ForEachElement element : elements) {
            if(element.getParent() != null) countWithParent++;
            Assertions.assertEquals("list-of-values", element.condition);
        }

        Assertions.assertEquals(1, countWithParent);
    }

    @Test
    void testDepthParserDeeper() {
        List<ForEachElement> elements = PARSER.buildDepthElements(HTML_DEEPER);
        Assertions.assertEquals(3, elements.size());

        int countWithParent = 0;

        for (ForEachElement element : elements) {
            if(element.getParent() != null) countWithParent++;
            Assertions.assertEquals("list-of-values", element.condition);
        }

        Assertions.assertEquals(2, countWithParent);
    }

    @Test
    void testDepthParserDeeperMultipleFors() {
        System.out.println(HTML_MULTIPLE_FORS.replace('\n', ' '));
        List<ForEachElement> elements = PARSER.buildDepthElements(HTML_MULTIPLE_FORS);
        Assertions.assertEquals(3, elements.size());

        int countWithParent = 0;

        for (ForEachElement element : elements) {
            if(element.getParent() != null) countWithParent++;
            Assertions.assertEquals("list-of-values", element.condition);
        }

        Assertions.assertEquals(1, countWithParent);
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

    //[$foreach $persons] [$foreach $fruits][$this.each][$end for] [$end for]
    @Test
    void testNestedThisEachValueReplacement() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("fruits", Collections.singletonList("Orange"));
        variables.put("persons", Collections.singletonList("John"));
        InjectionResult result = TAG.injectWithVariables(new InjectionResult(HTML_WITH_EACH), variables);
        System.out.println(result.getHtml());

        Assertions.assertFalse(result.getHtml().contains("John"));
        Assertions.assertTrue(result.getHtml().contains("Orange"));
    }

    @Test
    void testNestedParentParentEachValueReplacement() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("fruits", Collections.singletonList("Orange"));
        variables.put("persons", Collections.singletonList("John"));
        InjectionResult result = TAG.injectWithVariables(new InjectionResult(HTML_WITH_EACH_PARENT), variables);
        System.out.println(result.getHtml());

        Assertions.assertTrue(result.getHtml().contains("John"));
        Assertions.assertFalse(result.getHtml().contains("Orange"));
    }

    @Test
    void testBug() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("parent", new ArrayList<>(Arrays.asList(1, 2, 3, 4)));
        variables.put("child", new ArrayList<>(Arrays.asList("A","B")));

        InjectionResult result = new Iteration2Tag().injectWithVariables(new InjectionResult(HTML_BUG), variables);
        System.out.println(result.getHtml());

        Assertions.assertTrue(result.getHtml().contains("1 - A"));
        Assertions.assertTrue(result.getHtml().contains("1 - B"));
        Assertions.assertTrue(result.getHtml().contains("2 - A"));
        Assertions.assertTrue(result.getHtml().contains("2 - B"));

        Assertions.assertTrue(result.getHtml().contains("3 - A"));
        Assertions.assertTrue(result.getHtml().contains("3 - B"));
        Assertions.assertTrue(result.getHtml().contains("4 - A"));
        Assertions.assertTrue(result.getHtml().contains("4 - B"));
    }

    @Test
    void testResolver() {
        Person person = new Person();
        person.name = "du";

        Div superParent = new Div(DUMMY_ELEM, "Me", null);
        Div parent = new Div(DUMMY_ELEM, person, superParent);
        Div child = new Div(new ForEachElement(DUMMY_BLOCK, "<p>[$parent.parent.each] - [$parent.each.name] - [$this.each]</p>"), "sa", parent);

        String resolved = DivResolver.resolve(child);
        System.out.println(resolved);
        Assertions.assertEquals("<p>Me - du - sa</p>", resolved);
    }

    @Test
    void testResolver2() {
        Div superParent = new Div(DUMMY_ELEM, "Me", null);
        Div parent = new Div(DUMMY_ELEM, "du", superParent);
        Div child = new Div(new ForEachElement(DUMMY_BLOCK, "<p>[$parent.parent.each] - [$parent.each] - [$this.each]</p>"), "sa", parent);

        String resolved = DivResolver.resolve(child);
        System.out.println(resolved);
        Assertions.assertEquals("<p>Me - du - sa</p>", resolved);
    }

    static class Person {
        String name;

        public String getName() {
            return name;
        }
    }

    long countOccurrences(String block) {
        return Arrays.stream(block.split("<template")).count()-1;
    }
}
