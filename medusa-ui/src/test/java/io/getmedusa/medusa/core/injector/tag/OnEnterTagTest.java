package io.getmedusa.medusa.core.injector.tag;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class OnEnterTagTest extends AbstractTest {

    public static final String HTML = "<input m:onenter=\"sayHelloTo('John Doe')\" type=\"text\" name=\"term\" autocomplete=\"off\" />";
    public static final String HTML_DOUBLE_QUOTES = "<input m:onenter='sayHelloTo(\"Jane Doe\", 2)' type=\"text\" name=\"term\" autocomplete=\"off\" />";
    public static final String HTML_BOOLEAN = "<input m:onenter='turnOn(true)' type=\"text\" name=\"term\" autocomplete=\"off\" />";
    public static final String HTML_INTEGER = "<input m:onenter='increaseCount(2)' type=\"text\" name=\"term\" autocomplete=\"off\" />";
    public static final String HTML_OBJECT = "<input m:onenter='addName(person.name)' type=\"text\" name=\"term\" autocomplete=\"off\" />";
    public static final String HTML_OBJECT_MULTI = "<input m:onenter='addName(1, person.name, 1)' type=\"text\" name=\"term\" autocomplete=\"off\" />";
    private static final String HTML_EACH_ITERATION = """
                    <m:foreach collection="people" eachName="person">
                        <input m:onenter='addName("你好世界", 1)' type="text" name="term" autocomplete="off" />
                    </m:foreach>
            """;

    private static final String HTML_EACH_ITERATION_W_OBJECT = """
                    <m:foreach collection="people" eachName="person">
                        <input m:onenter='addName(person.name, 1)' type="text" name="term" autocomplete="off" />
                    </m:foreach>
            """;

    @Test
    void testSimple() {
        Document document = inject(HTML, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:onenter"), "m:onenter should be replaced with an onkeyup");
        Assertions.assertTrue(html.contains("onkeyup=\"_M.onEnter(this, 'sayHelloTo(\\'John Doe\\')', event"));
    }

    @Test
    void testDoubleQuotes() {
        Document document = inject(HTML_DOUBLE_QUOTES, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:onenter"), "m:onenter should be replaced with an onkeyup");
        Assertions.assertTrue(html.contains("onkeyup=\"_M.onEnter(this, 'sayHelloTo(\\'Jane Doe\\', 2)'"));
    }

    @Test
    void testBoolean() {
        Document document = inject(HTML_BOOLEAN, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:onenter"), "m:onenter should be replaced with an onkeyup");
        Assertions.assertTrue(html.contains("onkeyup=\"_M.onEnter(this, 'turnOn(true)'"));
    }

    @Test
    void testInteger() {
        Document document = inject(HTML_INTEGER, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:onenter"), "m:onenter should be replaced with an onkeyup");
        Assertions.assertTrue(html.contains("onkeyup=\"_M.onEnter(this, 'increaseCount(2)'"));
    }

    @Test
    void testObjectSingleParam() {
        Document document = inject(HTML_OBJECT, Map.of("person", new Person("안녕하세요 세계")));
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:onenter"), "m:onenter should be replaced with an onkeyup");
        Assertions.assertTrue(html.contains("onkeyup=\"_M.onEnter(this, 'addName(\\'안녕하세요 세계\\')'"));
    }

    @Test
    void testObjectMultipleParams() {
        Document document = inject(HTML_OBJECT_MULTI, Map.of("person", new Person("안녕하세요 세계")));
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:onenter"), "m:onenter should be replaced with an onkeyup");
        Assertions.assertTrue(html.contains("onkeyup=\"_M.onEnter(this, 'addName(1, \\'안녕하세요 세계\\', 1)'"));
    }

    @Test
    void testIteration() {
        Document document = inject(HTML_EACH_ITERATION, Map.of("people", List.of(new Person(""))));
        removeNonDisplayedElements(document);

        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:onenter"), "m:onenter should be replaced with an onkeyup");
        Assertions.assertTrue(html.contains("onkeyup=\"_M.onEnter(this, 'addName(\\'你好世界\\', 1)"));
    }

    @Test
    void testIterationEach() {
        Document document = inject(HTML_EACH_ITERATION_W_OBJECT, Map.of("people", List.of(new Person("안녕하세요 세계"))));
        removeNonDisplayedElements(document);

        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:onenter"), "m:onenter should be replaced with an onkeyup");
        Assertions.assertTrue(html.contains("onkeyup=\"_M.onEnter(this, 'addName(\\'안녕하세요 세계\\', 1)"));
    }

}
