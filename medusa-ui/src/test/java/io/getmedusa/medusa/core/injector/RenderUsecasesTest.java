package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.annotation.UIEventWithAttributes;
import io.getmedusa.medusa.core.injector.tag.AbstractTest;
import io.getmedusa.medusa.core.registry.EventHandlerRegistry;
import io.getmedusa.medusa.core.util.SecurityContext;
import io.getmedusa.medusa.core.util.TestRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class RenderUsecasesTest extends AbstractTest {

    private SecurityContext securityContext = new SecurityContext(null);

    @Test
    void testSimpleIf() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("counter-value", 3)));
        String nestedIf =
                """
               <m:if condition="counter-value" gt="5">
                   <p>Counter is above 5</p>
                </m:if>
                """;

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("<m:if"));
        Assertions.assertFalse(result.contains("</m:if>"));
    }
    
    @Test
    void testStandardHTMLInjection() {
        final String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, """
                <body>
                <h1>Hello Medusa</h1>
                <m:if condition="3" gt="5">
                    <p>Counter is above 5</p>
                </m:if>
                <p>Counter: <span></span></p>
                <button m:click="increaseCounter(2)">Increase counter</button>
                </body>
        """);

        System.out.println(result);
        Assertions.assertFalse(result.contains("m:click"));
    }

    @Test
    void testNestedIfs() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("counter-value", 3)));
        String nestedIf = """
                <m:if condition="counter-value" gt="2">
                    <m:if condition="counter-value" gt="5">
                        <p>Counter is above 5</p>
                    </m:if>
                </m:if>""";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("<m:if"));
        Assertions.assertFalse(result.contains("</m:if>"));
    }

    @Test
    void testSimpleForeach() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("counter-value", 3)));
        String nestedIf = "<m:foreach collection=\"counter-value\"><p>Hello, Medusa</p></m:foreach>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("<m:foreach"));
        Assertions.assertFalse(result.contains("</m:foreach>"));
        Assertions.assertEquals(3 + 1, countOccurrences(result, "Hello, Medusa")); //1 template + counter-value
    }

    @Test
    void testCombinationOuterIfNestedForEach() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("counter-value", 2)));
        String nestedIf = """
                <m:if condition="counter-value" gt="2">
                    <m:foreach collection="counter-value">
                        <p>Hello, Medusa</p>
                    </m:foreach>
                </m:if>""";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("<m:if"));
        Assertions.assertFalse(result.contains("</m:if>"));
        Assertions.assertFalse(result.contains("<m:foreach"));
        Assertions.assertFalse(result.contains("</m:foreach>"));
        Assertions.assertEquals(2 + 1, countOccurrences(result, "Hello, Medusa")); //1 template + counter-value
    }

    @Test
    void testCombinationOuterForeachNestedIf() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("counter-value", 2)));
        String nestedIf =
                """
                        <m:foreach collection="counter-value">
                            <m:if condition="counter-value" gt="2">
                                <p>Hello, Medusa</p>
                            </m:if>
                        </m:foreach>""";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("<m:if"));
        Assertions.assertFalse(result.contains("</m:if>"));
        Assertions.assertFalse(result.contains("<m:foreach"));
        Assertions.assertFalse(result.contains("</m:foreach>"));
        Assertions.assertEquals(2 + 1, countOccurrences(result, "Hello, Medusa")); //1 template + counter-value
    }

    @Test
    void testForeachWithObjectAsEach() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("counter-list", Arrays.asList("Zeus", "Poseidon", "Hera"))));
        String nestedIf = """
                <m:foreach collection="counter-list" eachName="name">
                <p>Hello, <m:text item="name" /></p>
                </m:foreach>""";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("<m:foreach"));
        Assertions.assertFalse(result.contains("</m:foreach>"));

        Assertions.assertTrue(result.contains("Hello, <span from-value=\"name\">Zeus</span></p>"));
        Assertions.assertTrue(result.contains("Hello, <span from-value=\"name\">Poseidon</span></p>"));
        Assertions.assertTrue(result.contains("Hello, <span from-value=\"name\">Hera</span></p>"));
    }

    @Test
    void testForeachWithIndexAsEach() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("counter-value", 3)));
        String nestedIf = """
                <m:foreach collection="counter-value" eachName="name">
                    <p>Hello, Medusa <m:text item="name" /></p>
                </m:foreach>""";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("<m:foreach"));
        Assertions.assertFalse(result.contains("</m:foreach>"));

        Assertions.assertEquals(3 + 1, countOccurrences(result, "Hello, Medusa")); //1 template + counter-value
        Assertions.assertTrue(result.contains("Medusa <span from-value=\"name\">0</span></p>"));
        Assertions.assertTrue(result.contains("Medusa <span from-value=\"name\">1</span></p>"));
        Assertions.assertTrue(result.contains("Medusa <span from-value=\"name\">2</span></p>"));
    }

    @Test
    void testForeachWithTraversableObjectAsEach() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("object-list", Arrays.asList(new ExampleClass(new ExampleClass(3355)), new ExampleClass(new ExampleClass(4512))))));
        String nestedIf = """
                <m:foreach collection="object-list" eachName="each">
                <p>Hello, <m:text item="each.innerClass.number" /></p>
                </m:foreach>""";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("<m:foreach"));
        Assertions.assertFalse(result.contains("</m:foreach>"));

        Assertions.assertTrue(result.contains("Hello, <span from-value=\"each.innerClass.number\">3355</span></p>"));
        Assertions.assertTrue(result.contains("Hello, <span from-value=\"each.innerClass.number\">4512</span></p>"));
    }

    @Test
    void testIfWithObjectTraversal() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("obj-value", new ExampleClass(987))));
        String nestedIf =
                """     
                        <m:if condition="obj-value.number" gt="5">
                           <p>Counter is above 5</p>
                        </m:if>""";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println(result);

        Assertions.assertFalse(result.contains("<m:if"));
        Assertions.assertFalse(result.contains("</m:if>"));
    }

    @Test
    void testValueSimple() {
        final String htmlFileName = path();
        Map<String, Object> names = new HashMap<>();
        names.put("name-per", "Perseus");
        names.put("name-med", "Medusa");
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(names));
        String divContent = "<div>Above the sea that cries and breaks, Swift <m:text item=\"name-per\" /> with <m:text item=\"name-med\" />'s snakes, Set free the maiden white like snow</div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, divContent);
        System.out.println(result);

        Assertions.assertTrue(result.contains("Above the sea that cries and breaks, Swift <span from-value=\"name-per\">Perseus</span> with <span from-value=\"name-med\">Medusa</span>'s snakes, Set free the maiden white like snow"));
    }

    @Test
    void testValueComplex() {
        final String htmlFileName = path();
        Map<String, Object> names = new HashMap<>();
        names.put("wrapper1", new ExampleClass(14132));
        names.put("wrapper2", new ExampleClass(89452));
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(names));
        String divContent = "<div>1264135664 can be achieved by multiplying <m:text item=\"wrapper1.number\" /> with <m:text item=\"wrapper2.number\" />, though you might need a calculator</div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, divContent);
        System.out.println(result);

        Assertions.assertTrue(result.contains("1264135664 can be achieved by multiplying <span from-value=\"wrapper1.number\">14132</span> with <span from-value=\"wrapper2.number\">89452</span>, though you might need a calculator"));
    }

    @Test
    void testConditionalClassAppendClassExists() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("clazz", "wide")));
        String nestedIf = "<div id=\"example-color-block\" class=\"color-block\" m:class-append=\"clazz\"></div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println("result: " + result);

        Assertions.assertFalse(result.contains("m:class-append"));
        Assertions.assertTrue(result.contains("<div id=\"example-color-block\" class=\"color-block wide\" data-base-class=\"color-block\" data-from=\"clazz\"></div>"));
    }

    @Test
    void testConditionalClassAppendClassDoesNotExist() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("clazz", "wide")));
        String nestedIf = "<div id=\"example-color-block\" m:class-append=\"clazz\"></div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println("result: " + result);

        Assertions.assertFalse(result.contains("m:class-append"));
        Assertions.assertTrue(result.contains("<div id=\"example-color-block\" data-base-class=\"\" data-from=\"clazz\" class=\"wide\"></div>"));
    }

    @Test
    void testConditionalClassWithObjectTraversal() {
        final String htmlFileName = path();
        EventHandlerRegistry.getInstance().add(htmlFileName, new HandlerImpl(Collections.singletonMap("wrapper1", new Person("wide"))));
        String nestedIf = "<div id=\"example-color-block\" class=\"color-block\" m:class-append=\"wrapper1.name\"></div>";

        String result = HTMLInjector.INSTANCE.htmlStringInject(new TestRequest(), securityContext, nestedIf);
        System.out.println("result: " + result);

        Assertions.assertFalse(result.contains("m:class-append"));
        Assertions.assertTrue(result.contains("<div id=\"example-color-block\" class=\"color-block wide\" data-base-class=\"color-block\" data-from=\""));
    }

    // utility

    private int countOccurrences(String result, String pattern) {
        return result.split(pattern).length - 1;
    }

    private String path() {
        return "/test";
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
    public static class HandlerImpl implements UIEventWithAttributes {

        private final Map<String, Object> variables;

        HandlerImpl(Map<String, Object> variables) {
            this.variables = variables;
        }

        @Override
        public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
            return new PageAttributes(variables);
        }
    }

}
