package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class NestedIterationTagTest {

    private static final IterationTag TAG = new IterationTag();
    public static final String HTML =
            "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<body>\n" +
                "[$foreach $list-of-values]" +
                    "0" +
                    "[$foreach $list-of-values]1[$end for]" +
                    "2" +
                "[$end for]\n" +
            "</body>\n" +
            "</html>";

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
        Assertions.assertEquals(3, countOccurrences(result.getHtml()));
    }

    @Test
    void testMultipleElementList() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", Arrays.asList(1,2,3,4,5));
        InjectionResult result = TAG.injectWithVariables(new InjectionResult(HTML), variables);
        System.out.println(result.getHtml());
        Assertions.assertFalse(result.getHtml().contains("$foreach"));
        Assertions.assertTrue(result.getHtml().contains("<template") && result.getHtml().contains("</template>"));
        Assertions.assertEquals(7, countOccurrences(result.getHtml()));
    }

    long countOccurrences(String block) {
        return Arrays.stream(block.split("<template")).count()-1;
    }
}
