package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;

class IterationTagTest {

    private static final IterationTag TAG = new IterationTag();
    public static final String HTML =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
            "[$foreach $list-of-values]<p>Medusa</p>[$end]" +
            "</body>\n" +
            "</html>";

    @Test
    void testMatcher() {
        Matcher matcher = TAG.buildBlockMatcher(HTML);
        Assertions.assertTrue(matcher.find());
        Assertions.assertEquals("[$foreach $list-of-values]<p>Medusa</p>[$end]", matcher.group(0));
        Assertions.assertFalse(matcher.find());
    }

    @Test
    void testParse() {
        String block = "[$foreach $list-of-values]<p>Medusa</p>[$end]<p>Medusa</p>[$end]";
        String condition = TAG.parseCondition(block);
        Assertions.assertEquals("list-of-values", condition);

        String blockInner = TAG.parseInnerBlock(block);
        Assertions.assertEquals("<p>Medusa</p>", blockInner);
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

    long countOccurrences(String block) {
        return Arrays.stream(block.split("<p>Medusa</p>")).count()-1;
    }

}
