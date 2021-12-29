package io.getmedusa.medusa.core.injector.tag;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class ClickTagTest extends AbstractTest {

    public static final String HTML = "<button m:click=\"sayHelloTo('John Doe')\">Hello!</button>";
    public static final String HTML_DOUBLE_QUOTES = "<button m:click='sayHelloTo(\"Jane Doe\", 2)'>Hello!</button>";
    public static final String HTML_BOOLEAN = "<button m:click='turnOn(true)'>Lightswitch ON</button>";
    public static final String HTML_INTEGER = "<button m:click='increaseCount(2)'>Add two people</button>";
    public static final String HTML_OBJECT = "<button m:click='addName(person.name)'>Add name</button>";
    public static final String HTML_OBJECT_MULTI = "<button m:click='addName(1, person.name, 1)'>Add name</button>";
    private static final String HTML_EACH_ITERATION = """
                    <m:foreach collection="people" eachName="person">
                        <button m:click='addName("你好世界", 1)'>Add name</button>
                    </m:foreach>
            """;

    private static final String HTML_EACH_ITERATION_W_OBJECT = """
                    <m:foreach collection="people" eachName="person">
                        <button m:click='addName(person.name, 1)'>Add name</button>
                    </m:foreach>
            """;

    @Test
    void testSimple() {
        Document document = inject(HTML, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:click"), "m:click should be replaced with an onclick");
        Assertions.assertTrue(html.contains("onclick=\"_M.sendEvent(this, 'sayHelloTo(\\'John Doe\\')')\""));
    }

    @Test
    void testDoubleQuotes() {
        Document document = inject(HTML_DOUBLE_QUOTES, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:click"), "m:click should be replaced with an onclick");
        Assertions.assertTrue(html.contains("onclick=\"_M.sendEvent(this, 'sayHelloTo(\\'Jane Doe\\', 2)')\""));
    }

    @Test
    void testBoolean() {
        Document document = inject(HTML_BOOLEAN, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:click"), "m:click should be replaced with an onclick");
        Assertions.assertTrue(html.contains("onclick=\"_M.sendEvent(this, 'turnOn(true)')\""));
    }

    @Test
    void testInteger() {
        Document document = inject(HTML_INTEGER, Collections.emptyMap());
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:click"), "m:click should be replaced with an onclick");
        Assertions.assertTrue(html.contains("onclick=\"_M.sendEvent(this, 'increaseCount(2)')\""));
    }

    @Test
    void testObjectSingleParam() {
        Document document = inject(HTML_OBJECT, Map.of("person", new Person("안녕하세요 세계")));
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:click"), "m:click should be replaced with an onclick");
        Assertions.assertTrue(html.contains("onclick=\"_M.sendEvent(this, 'addName(\\'안녕하세요 세계\\')')\""));
    }

    @Test
    void testObjectMultipleParams() {
        Document document = inject(HTML_OBJECT_MULTI, Map.of("person", new Person("안녕하세요 세계")));
        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:click"), "m:click should be replaced with an onclick");
        Assertions.assertTrue(html.contains("onclick=\"_M.sendEvent(this, 'addName(1, \\'안녕하세요 세계\\', 1)')\""));
    }

    @Test
    void testIteration() {
        Document document = inject(HTML_EACH_ITERATION, Map.of("people", List.of(new Person(""))));
        removeNonDisplayedElements(document);

        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:click"), "m:click should be replaced with an onclick");
        Assertions.assertTrue(html.contains("onclick=\"_M.sendEvent(this, 'addName(\\'你好世界\\', 1)"));
    }

    @Test
    void testIterationEach() {
        Document document = inject(HTML_EACH_ITERATION_W_OBJECT, Map.of("people", List.of(new Person("안녕하세요 세계"))));
        removeNonDisplayedElements(document);

        String html = document.html();
        System.out.println(html);

        Assertions.assertFalse(html.contains("m:click"), "m:click should be replaced with an onclick");
        Assertions.assertTrue(html.contains("onclick=\"_M.sendEvent(this, 'addName(\\'안녕하세요 세계\\', 1)"));
    }

}
