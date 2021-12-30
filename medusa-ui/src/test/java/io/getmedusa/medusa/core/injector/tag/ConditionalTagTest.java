package io.getmedusa.medusa.core.injector.tag;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ConditionalTagTest extends AbstractTest {

    private static final String HTML_EQ_SIMPLE = """
                    <m:if condition="some-variable" eq="'a'">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_EQ_ELSE = """
                    <m:if condition="some-variable" eq="'a'">
                        <p>A</p>
                        
                        <m:else>
                            <p>B</p>
                        </m:else>
                    </m:if>
            """;

    private static final String HTML_EQ_ELSE_IF = """
                    <m:if condition="some-variable" eq="'a'">
                        <p>A</p>
                        <m:elseif condition="some-other-variable" eq="1">
                            <p>B</p>
                        </m:elseif>
                        <m:else>
                            <p>C</p>
                        </m:else>
                    </m:if>
            """;

    private static final String HTML_EQ_VARIABLE = """
                    <m:if condition="some-variable.name" eq="a.name">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_EQ_BOOL = """
                    <m:if condition="some-variable" eq="true">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_EQ_ELSE_IF_NESTED = """
                    <m:if condition="some-variable" eq="'a'">
                        <m:if condition="another-variable" eq="'a'">
                            <p>A</p>
                            <m:elseif condition="some-other-variable" eq="1">
                                <p>B</p>
                            </m:elseif>
                            <m:else>
                                <p>C</p>
                            </m:else>
                        </m:if>
                        <m:elseif condition="some-other-variable" eq="1">
                            <p>B</p>
                        </m:elseif>
                        <m:else>
                            <p>C</p>
                        </m:else>
                    </m:if>
            """;

    private static final String HTML_EQ_SIMPLE_ITERATION = """
                    <m:foreach collection="list-of-values">
                        <m:if condition="some-variable" eq="1">
                            <p>A</p>
                        </m:if>
                    </m:foreach>
            """;

    private static final String HTML_EQ_EACH_ITERATION = """
                    <m:foreach collection="list-of-values" eachName="myItem">
                        <m:if condition="myItem" eq="1">
                            <p>A</p>
                        </m:if>
                    </m:foreach>
            """;

    private static final String HTML_EQ_EACH_ITERATION_W_OBJECT = """
                    <m:foreach collection="people" eachName="person">
                        <m:if condition="person.name" eq="'John'">
                            <p>This is John</p>
                        </m:if>
                    </m:foreach>
            """;

    private static final String HTML_GT_SIMPLE = """
                    <m:if condition="some-variable" gt="1">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_LT_SIMPLE = """
                    <m:if condition="some-variable" lt="10">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_GTE_SIMPLE = """
                    <m:if condition="some-variable" gte="5">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_LTE_SIMPLE = """
                    <m:if condition="some-variable" lte="5">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_NOT_SIMPLE = """
                    <m:if condition="some-variable" not="5">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_RANGE_SIMPLE = """
                    <m:if condition="some-variable" lte="50" gt="0">
                        <p>A</p>
                    </m:if>
            """;

    private static final String HTML_RANGE_SWAPPED = """
                    <m:if condition="some-variable" gt="1" lte="50">
                        <p>A</p>
                    </m:if>
            """;

    @Test
    void testSimpleHTML() {
        Document htmlA = inject(HTML_EQ_SIMPLE, Map.of("some-variable", "a"));
        Document htmlB = inject(HTML_EQ_SIMPLE, Map.of("some-variable", "b"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testElse() {
        Document htmlA = inject(HTML_EQ_ELSE, Map.of("some-variable", "a"));
        Document htmlB = inject(HTML_EQ_ELSE, Map.of("some-variable", "b"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("B", htmlB.text());
    }

    @Test
    void testElseIf() {
        Document htmlA = inject(HTML_EQ_ELSE_IF, Map.of("some-variable", "a", "some-other-variable", "1"));
        Document htmlB = inject(HTML_EQ_ELSE_IF, Map.of("some-variable", "b", "some-other-variable", "1"));
        Document htmlC = inject(HTML_EQ_ELSE_IF, Map.of("some-variable", "c", "some-other-variable", "2"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);
        removeNonDisplayedElements(htmlC);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());
        System.out.println(htmlC.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("B", htmlB.text());
        Assertions.assertEquals("C", htmlC.text());
    }

    @Test
    void testElseIfNested() {
        Document htmlA = inject(HTML_EQ_ELSE_IF_NESTED, Map.of("some-variable", "'a'", "another-variable", "'a'", "some-other-variable", "1"));
        Document htmlB = inject(HTML_EQ_ELSE_IF_NESTED, Map.of("some-variable", "'b'", "another-variable", "'a'","some-other-variable", "1"));
        Document htmlC = inject(HTML_EQ_ELSE_IF_NESTED, Map.of("some-variable", "'c'", "another-variable", "'a'","some-other-variable", "2"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);
        removeNonDisplayedElements(htmlC);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());
        System.out.println(htmlC.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("B", htmlB.text());
        Assertions.assertEquals("C", htmlC.text());
    }

    @Test
    void testSimpleGT() {
        Document htmlA = inject(HTML_GT_SIMPLE, Map.of("some-variable", "5"));
        Document htmlB = inject(HTML_GT_SIMPLE, Map.of("some-variable", "0"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testSimpleLT() {
        Document htmlA = inject(HTML_LT_SIMPLE, Map.of("some-variable", "5"));
        Document htmlB = inject(HTML_LT_SIMPLE, Map.of("some-variable", "15"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testSimpleGTE() {
        Document htmlA = inject(HTML_GTE_SIMPLE, Map.of("some-variable", "5"));
        Document htmlB = inject(HTML_GTE_SIMPLE, Map.of("some-variable", "1"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testSimpleLTE() {
        Document htmlA = inject(HTML_LTE_SIMPLE, Map.of("some-variable", "5"));
        Document htmlB = inject(HTML_LTE_SIMPLE, Map.of("some-variable", "20"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testSimpleNOT() {
        Document htmlA = inject(HTML_NOT_SIMPLE, Map.of("some-variable", "1"));
        Document htmlB = inject(HTML_NOT_SIMPLE, Map.of("some-variable", "5"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testSimpleRange() {
        Document htmlA = inject(HTML_RANGE_SIMPLE, Map.of("some-variable", "5"));
        Document htmlB = inject(HTML_RANGE_SIMPLE, Map.of("some-variable", "500"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testSwappedRange() {
        Document htmlA = inject(HTML_RANGE_SWAPPED, Map.of("some-variable", "5"));
        Document htmlB = inject(HTML_RANGE_SWAPPED, Map.of("some-variable", "500"));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testSimpleHTMLVariable() {
        Document htmlA = inject(HTML_EQ_VARIABLE, Map.of("some-variable", new Person("こんにちは世界"), "a", new Person("こんにちは世界")));
        Document htmlB = inject(HTML_EQ_VARIABLE, Map.of("some-variable", new Person("नमस्ते दुनिया"), "a", new Person("こんにちは世界")));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testSimpleHTMLBoolean() {
        Document htmlA = inject(HTML_EQ_BOOL, Map.of("some-variable", true));
        Document htmlB = inject(HTML_EQ_BOOL, Map.of("some-variable", false));

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testSimpleIteration() {
        final Map<String, Object> variablesA = Map.of("list-of-values", List.of(1, 2), "some-variable", 1);
        final Map<String, Object> variablesB = Map.of("list-of-values", List.of(1, 2), "some-variable", 2);

        Document htmlA =  inject(HTML_EQ_SIMPLE_ITERATION, variablesA);
        Document htmlB = inject(HTML_EQ_SIMPLE_ITERATION, variablesB);

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A A", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testEachIteration() {
        final Map<String, Object> variablesA = Map.of("list-of-values", List.of(1, 1));
        final Map<String, Object> variablesB = Map.of("list-of-values", List.of(1, 2, 3));

        Document htmlA = inject(HTML_EQ_EACH_ITERATION, variablesA);
        Document htmlB = inject(HTML_EQ_EACH_ITERATION, variablesB);

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("A A", htmlA.text());
        Assertions.assertEquals("A", htmlB.text());
    }

    @Test
    void testEachIterationWithObject() {
        final Map<String, Object> variablesA = Map.of("people", List.of(new Person("Jane"), new Person("John"), new Person("Peter")));
        final Map<String, Object> variablesB = Map.of("people", List.of(new Person("A"), new Person("B"), new Person("C")));

        Document htmlA =  inject(HTML_EQ_EACH_ITERATION_W_OBJECT, variablesA);
        Document htmlB =  inject(HTML_EQ_EACH_ITERATION_W_OBJECT, variablesB);

        removeNonDisplayedElements(htmlA);
        removeNonDisplayedElements(htmlB);

        System.out.println(htmlA.html());
        System.out.println(htmlB.html());

        Assertions.assertEquals("This is John", htmlA.text());
        Assertions.assertEquals("", htmlB.text());
    }

    @Test
    void testElseConditionGeneration() {
        String conditionElse = new ConditionalTag().oppositeOfCombinedConditions(List.of("X == 1", "X == 2"));
        Assertions.assertEquals("!(X == 1 || X == 2)", conditionElse);
    }
}
