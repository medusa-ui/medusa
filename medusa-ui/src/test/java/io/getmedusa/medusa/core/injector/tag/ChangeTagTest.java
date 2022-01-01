package io.getmedusa.medusa.core.injector.tag;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class ChangeTagTest extends AbstractTest {

    public static final String HTML = "<input m:change=\"sayHelloTo('John Doe')\" />";
    public static final String HTML_DOUBLE_QUOTES = "<input m:change='sayHelloTo(\"Jane Doe\", 2)' />";
    public static final String HTML_BOOLEAN = "<input m:change='turnOn(true)' />";
    public static final String HTML_INTEGER = "<input m:change='increaseCount(2)' />";
    public static final String HTML_OBJECT = "<input m:change='addName(person.name)' />";
    public static final String HTML_OBJECT_MULTI = "<input m:change='addName(1, person.name, 1)' />";
    private static final String HTML_EACH_ITERATION = """
                    <m:foreach collection="people" eachName="person">
                        <input m:change="search()" name="term" />
                    </m:foreach>
            """;

    private static final String HTML_EACH_ITERATION_W_OBJECT = """
                    <m:foreach collection="people" eachName="person">
                        <input m:change='addName(person.name, 1)' />
                    </m:foreach>
            """;

    @Test
    void testSimple() {
        Document document = inject(HTML, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:change"), "m:change should be replaced with an onclick");
        Assertions.assertTrue(html.contains("oninput=\"_M.sendEvent(this, 'sayHelloTo(\\'John Doe\\')')\""));
    }

    @Test
    void testDoubleQuotes() {
        Document document = inject(HTML_DOUBLE_QUOTES, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:change"), "m:change should be replaced with an onclick");
        Assertions.assertTrue(html.contains("oninput=\"_M.sendEvent(this, 'sayHelloTo(\\'Jane Doe\\', 2)')\""));
    }

    @Test
    void testBoolean() {
        Document document = inject(HTML_BOOLEAN, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:change"), "m:change should be replaced with an onclick");
        Assertions.assertTrue(html.contains("oninput=\"_M.sendEvent(this, 'turnOn(true)')\""));
    }

    @Test
    void testInteger() {
        Document document = inject(HTML_INTEGER, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:change"), "m:change should be replaced with an onclick");
        Assertions.assertTrue(html.contains("oninput=\"_M.sendEvent(this, 'increaseCount(2)')\""));
    }

    @Test
    void testObjectSingleParam() {
        Document document = inject(HTML_OBJECT, Map.of("person", new AbstractTest.Person("안녕하세요 세계")));
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:change"), "m:change should be replaced with an onclick");
        Assertions.assertTrue(html.contains("oninput=\"_M.sendEvent(this, 'addName(person.name)')\""));
    }

    @Test
    void testObjectMultipleParams() {
        Document document = inject(HTML_OBJECT_MULTI, Map.of("person", new AbstractTest.Person("안녕하세요 세계")));
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:change"), "m:change should be replaced with an onclick");
        Assertions.assertTrue(html.contains("oninput=\"_M.sendEvent(this, 'addName(1, person.name, 1)')\""));
    }

    @Test
    void testIteration() {
        Document document = inject(HTML_EACH_ITERATION, Map.of("people", List.of(new AbstractTest.Person(""))));
        removeNonDisplayedElements(document);

        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:change"), "m:change should be replaced with an onclick");
        Assertions.assertTrue(html.contains("oninput=\"_M.sendEvent(this, 'search()')"));
    }

    @Test
    void testIterationEach() {
        Document document = inject(HTML_EACH_ITERATION_W_OBJECT, Map.of("people", List.of(new AbstractTest.Person("안녕하세요 세계"))));
        removeNonDisplayedElements(document);

        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:change"), "m:change should be replaced with an onclick");
        Assertions.assertTrue(html.contains("oninput=\"_M.sendEvent(this, 'addName(person.name, 1)"));
    }

}
