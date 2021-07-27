package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.security.Principal;
import java.util.*;

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
        Assertions.assertEquals(3+1, countOccurrences(result, "Hello, Medusa")); //1 template + counter-value
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
        Assertions.assertEquals(2+1, countOccurrences(result, "Hello, Medusa")); //1 template + counter-value
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
        Assertions.assertEquals(2+1, countOccurrences(result, "Hello, Medusa")); //1 template + counter-value
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
        Assertions.assertEquals(3+1, countOccurrences(result, "Hello, Medusa")); //1 template + counter-value
        Assertions.assertTrue(result.contains("Medusa 0</p>"));
        Assertions.assertTrue(result.contains("Medusa 1</p>"));
        Assertions.assertTrue(result.contains("Medusa 2</p>"));
    }

    @Test
    void testForeachWithTraversableObjectAsEach() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("object-list", Arrays.asList(new ExampleClass(new ExampleClass(3355)), new ExampleClass(new ExampleClass(4512))))));
        String nestedIf = "[$foreach $object-list]\n" +
                "<p>Hello, [$each.innerClass.number]</p>\n" +
                "[$end for]";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("[$foreach"));
        Assertions.assertFalse(result.contains("[$end"));

        Assertions.assertTrue(result.contains("Hello, 3355</p>"));
        Assertions.assertTrue(result.contains("Hello, 4512</p>"));
    }

    @Test
    void testIfWithObjectTraversal() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("obj-value", new ExampleClass(987))));
        String nestedIf =
                "[$if($obj-value.number > 5)]\n" +
                "   <p>Counter is above 5</p>\n" +
                "[$end if]";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("[$if"));
        Assertions.assertFalse(result.contains("[$end"));
    }

    @Test
    void testValueSimple() {
        final String htmlFileName = randomizedFileName();
        Map<String, Object> names = new HashMap<>();
        names.put("name-per", "Perseus");
        names.put("name-med", "Medusa");
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, names));
        String divContent = "<div>Above the sea that cries and breaks, Swift [$name-per] with [$name-med]'s snakes, Set free the maiden white like snow</div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, divContent);
        System.out.println(result);

        Assertions.assertEquals("<div>Above the sea that cries and breaks, Swift <span from-value=\"name-per\">Perseus</span> with <span from-value=\"name-med\">Medusa</span>'s snakes, Set free the maiden white like snow</div>", result);
    }

    @Test
    void testValueComplex() {
        final String htmlFileName = randomizedFileName();
        Map<String, Object> names = new HashMap<>();
        names.put("wrapper1", new ExampleClass(14132));
        names.put("wrapper2", new ExampleClass(89452));
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, names));
        String divContent = "<div>1264135664 can be achieved by multiplying [$wrapper1.number] with [$wrapper2.number], though you might need a calculator</div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, divContent);
        System.out.println(result);

        Assertions.assertEquals("<div>1264135664 can be achieved by multiplying <span from-value=\"wrapper1.number\">14132</span> with <span from-value=\"wrapper2.number\">89452</span>, though you might need a calculator</div>", result);
    }

    @Test
    void testConditionalClassAppendClassExists() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("counter-value", 5)));
        String nestedIf = "<div id=\"example-color-block\" class=\"color-block\" m-class-append=\"$counter-value > 2 ? 'wide' : 'square'\"></div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println("result: " + result);

        Assertions.assertFalse(result.contains("m-class-append"));
        Assertions.assertTrue(result.startsWith("<div id=\"example-color-block\" class=\"color-block wide\" data-base-class=\"color-block\" data-from=\""));
    }

    @Test
    void testConditionalClassAppendClassDoesNotExist() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("counter-value", 5)));
        String nestedIf = "<div id=\"example-color-block\" m-class-append=\"$counter-value > 2 ? 'wide' : 'square'\"></div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println("result: " + result);

        Assertions.assertFalse(result.contains("m-class-append"));
        Assertions.assertTrue(result.startsWith("<div id=\"example-color-block\" class=\" wide\" data-base-class=\"\" data-from=\""));
    }

    @Test
    void testConditionalClassWithObjectTraversal() {
        final String htmlFileName = randomizedFileName();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(htmlFileName, Collections.singletonMap("wrapper1", new ExampleClass(14132))));
        String nestedIf = "<div id=\"example-color-block\" class=\"color-block\" m-class-append=\"$wrapper1.number > 2 ? 'wide' : 'square'\"></div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(htmlFileName, nestedIf);
        System.out.println("result: " + result);

        Assertions.assertFalse(result.contains("m-class-append"));
        Assertions.assertTrue(result.startsWith("<div id=\"example-color-block\" class=\"color-block wide\" data-base-class=\"color-block\" data-from=\""));
    }

    // utility

    private int countOccurrences(String result, String pattern) {
        return result.split(pattern).length -1;
    }

    private String randomizedFileName() {
        return UUID.randomUUID().toString();
    }

    static class ExampleClass {

        private final ExampleClass innerClass;
        private final int number;

        public ExampleClass(ExampleClass innerClass) {
            this.number = 0;
            this.innerClass = innerClass;
        }

        public ExampleClass(int number) {
            this.number = number;
            this.innerClass = null;
        }

        public int getNumber() {
            return number;
        }

        public ExampleClass getInnerClass() {
            return innerClass;
        }
    }

    @UIEventPage(path = "/test", file = "xyz")
    static class HandlerImpl implements UIEventController {

        private final Map<String, Object> variables;
        HandlerImpl(String file, Map<String, Object> variables) {
            this.variables = variables;
        }

        @Override
        public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
            return new PageAttributes(variables);
        }
    }

}
