package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

class RenderUsecasesTest {

    @Test
    void testSimpleIf() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("counter-value", 3)));
        String nestedIf =
                "[$if($counter-value > 5)]\n" +
                "   <p>Counter is above 5</p>\n" +
                "[$end if]";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("[$if"));
        Assertions.assertFalse(result.contains("[$end"));
    }

    @Test
    void testNestedIfs() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("counter-value", 3)));
        String nestedIf = "[$if($counter-value > 2)]\n" +
                "    [$if($counter-value > 5)]\n" +
                "        <p>Counter is above 5</p>\n" +
                "    [$end if]\n" +
                "[$end if]";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("[$if"));
        Assertions.assertFalse(result.contains("[$end"));
    }

    @Test
    void testSimpleForeach() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("counter-value", 3)));
        String nestedIf = "[$foreach $counter-value]\n" +
                "<p>Hello, Medusa</p>\n" +
                "[$end for]";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("[$foreach"));
        Assertions.assertFalse(result.contains("[$end"));
        Assertions.assertEquals(3+1, countOccurences(result, "Hello, Medusa")); //1 template + counter-value
    }

    @Test
    void testCombinationOuterIfNestedForEach() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("counter-value", 2)));
        String nestedIf = "[$if($counter-value > 2)]\n" +
                          "    [$foreach $counter-value]\n" +
                          "        <p>Hello, Medusa</p>\n" +
                          "    [$end for]" +
                          "[$end if]";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("[$if"));
        Assertions.assertFalse(result.contains("[$foreach"));
        Assertions.assertFalse(result.contains("[$end"));
        Assertions.assertEquals(2+1, countOccurences(result, "Hello, Medusa")); //1 template + counter-value
    }

    @Test
    void testCombinationOuterForeachNestedIf() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("counter-value", 2)));
        String nestedIf =
                "[$foreach $counter-value]\n" +
                "    [$if($counter-value > 2)]\n" +
                "        <p>Hello, Medusa</p>\n" +
                "    [$end if]" +
                "[$end for]";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("[$if"));
        Assertions.assertFalse(result.contains("[$foreach"));
        Assertions.assertFalse(result.contains("[$end"));
        Assertions.assertEquals(2+1, countOccurences(result, "Hello, Medusa")); //1 template + counter-value
    }

    @Test
    void testForeachWithObjectAsEach() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("counter-list", Arrays.asList("Zeus", "Poseidon", "Hera"))));
        String nestedIf = "[$foreach $counter-list]\n" +
                "<p>Hello, [$each]</p>\n" +
                "[$end for]";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("[$foreach"));
        Assertions.assertFalse(result.contains("[$end"));

        Assertions.assertTrue(result.contains("Hello, Zeus</p>"));
        Assertions.assertTrue(result.contains("Hello, Poseidon</p>"));
        Assertions.assertTrue(result.contains("Hello, Hera</p>"));
    }

    @Test
    void testForeachWithIndexAsEach() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("counter-value", 3)));
        String nestedIf = "[$foreach $counter-value]\n" +
                "<p>Hello, Medusa [$each]</p>\n" +
                "[$end for]";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("[$foreach"));
        Assertions.assertFalse(result.contains("[$end"));
        Assertions.assertEquals(3+1, countOccurences(result, "Hello, Medusa")); //1 template + counter-value
        Assertions.assertTrue(result.contains("Medusa 0</p>"));
        Assertions.assertTrue(result.contains("Medusa 1</p>"));
        Assertions.assertTrue(result.contains("Medusa 2</p>"));
    }


    // utility

    private int countOccurences(String result, String pattern) {
        return result.split(pattern).length -1;
    }

    private String randomizedFileName() {
        return UUID.randomUUID().toString();
    }

    static class HandlerImpl implements UIEventController {

        private final String fileName;
        private final Map<String, Object> variables;
        HandlerImpl(String fileName, Map<String, Object> variables) {
            this.fileName = fileName;
            this.variables = variables;
        }

        @Override
        public PageSetup setupPage() {
            return new PageSetup("/test", fileName, variables);
        }
    }

}
