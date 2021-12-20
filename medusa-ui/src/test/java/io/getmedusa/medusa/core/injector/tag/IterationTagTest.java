package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

class IterationTagTest {

    private static final IterationTag TAG = new IterationTag();

    public static final String HTML =
            """
            <!DOCTYPE html>
            <html lang="en">
            <body>
            <m:foreach collection="list-of-values"><p>Medusa</p></m:foreach>
            </html>""";

    public static final String MULTIPLE_HTML =
            """
            <!DOCTYPE html>
            <html lang="en">
            <body>
            <m:foreach collection="list-of-values"><p>Medusa</p></m:foreach>
            <m:foreach collection="other-list-of-values"><p>Medusa</p></m:foreach>
            </html>""";

    public static final String HTML_NESTED =
            """
            <!DOCTYPE html>
            <html lang="en">
            <body>
            <m:foreach collection="outer-list">
                <p>outerSTART</p>
                <m:foreach collection="inner-list">
                    <p>Inner</p>
                </m:foreach>
                <p>outerEND</p>
            </m:foreach>
            </html>""";

    public static final String HTML_W_ELEMENT =
            """
            <!DOCTYPE html>
            <html lang="en">
            <body>
                <m:foreach collection="list-of-values" eachName="myItem">
                    <p>Each value: <m:text item="myItem" /></p>
                </m:foreach>
            </body>
            </html>
            """;

    public static final String HTML_W_ELEMENT_NESTED =
            """
            <!DOCTYPE html>
            <html lang="en">
            <body>
                <m:foreach collection="list-of-values" eachName="myItem">
                    <m:foreach collection="other-list-of-values" eachName="myItem2">
                        <p>Each value: <m:text item="myItem" /> :: <m:text item="myItem2" /></p>
                    </m:foreach>
                </m:foreach>
            </body>
            </html>
            """;

    public static final String HTML_W_ELEMENT_NESTED_AND_VALUE_AS_ATTRIBUTE = """
            <!DOCTYPE html>
            <html lang="en">
            <body>
                <m:foreach collection="list-of-values" eachName="myItem">
                    <m:foreach collection="other-list-of-values" eachName="myItem2">
                        <p>Each value: <m:text item="myItem" /> :: <input type="text" m:value="myItem2" /></p>
                    </m:foreach>
                </m:foreach>
            </body>
            </html>
            """;

    public static final String HTML_W_COMPLEX_ELEMENT_NESTED =
            """
            <!DOCTYPE html>
            <html lang="en">
            <body>
                <m:foreach collection="list-of-values" eachName="myItem">
                    <m:foreach collection="other-list-of-values" eachName="myItem2">
                        <p>Each value: <m:text item="myItem.exampleValue" /> :: <m:text item="myItem2.exampleValue" /></p>
                    </m:foreach>
                </m:foreach>
            </body>
            </html>
            """;

    public static final String HTML_W_ELEMENT_NESTED_AND_COMPLEX_VALUE_AS_ATTRIBUTE = """
            <!DOCTYPE html>
            <html lang="en">
            <body>
                <m:foreach collection="list-of-values" eachName="myItem">
                    <m:foreach collection="other-list-of-values" eachName="myItem2">
                        <p>Each value: <m:text item="myItem.exampleValue" /> :: <input type="text" m:value="myItem2.exampleValue" /></p>
                    </m:foreach>
                </m:foreach>
            </body>
            </html>
            """;

    private final ServerRequest request = MockServerRequest.builder().build();

    @Test
    void testSingleElementList() {
        System.out.println(HTML);
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", Collections.singletonList(1));
        InjectionResult result = TAG.inject(new InjectionResult(HTML), variables, request);
        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Assertions.assertEquals(2, countOccurrences(result.getHTML()));
    }

    @Test
    void testMultipleElementList() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", Arrays.asList(1,2,3,4,5));
        InjectionResult result = TAG.inject(new InjectionResult(HTML), variables, request);
        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Assertions.assertEquals(6, countOccurrences(result.getHTML()));
    }

    @Test
    void testEmptyList() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("list-of-values", new ArrayList<>());
        InjectionResult result = TAG.inject(new InjectionResult(HTML), variables, request);
        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Assertions.assertEquals(1, countOccurrences(result.getHTML()));
    }

    @Test
    void testMultipleLists() {
        Map<String, Object> variables = new HashMap<>();
        final List<Integer> listOfValues = Arrays.asList(1, 2, 3);
        final List<Integer> otherListOfValues = Arrays.asList(4, 5);
        variables.put("list-of-values", listOfValues);
        variables.put("other-list-of-values", otherListOfValues);

        InjectionResult result = TAG.inject(new InjectionResult(MULTIPLE_HTML), variables, request);
        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Assertions.assertEquals(listOfValues.size() + otherListOfValues.size() + variables.size(), countOccurrences(result.getHTML()));
    }

    @Test
    void testNestedList() {
        Map<String, Object> variables = new HashMap<>();
        final List<Integer> outerList = Arrays.asList(1, 2);
        final List<String> innerList = Arrays.asList("X", "Y", "Z");

        variables.put("outer-list", outerList);
        variables.put("inner-list", innerList);

        InjectionResult result = TAG.inject(new InjectionResult(HTML_NESTED), variables, request);
        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));

        final Document document = Jsoup.parse(result.getHTML());
        Map<String, List<Element>> templateElements = findDivsWithTemplate(document);

        Assertions.assertEquals(2, templateElements.keySet().size());
        final String[] keySet = templateElements.keySet().toArray(new String[0]);
        int sizeFirst = templateElements.get(keySet[0]).size();
        int sizeSecond = templateElements.get(keySet[1]).size();
        int sizeBiggest = Math.max(sizeFirst, sizeSecond);
        int sizeSmallest = Math.min(sizeFirst, sizeSecond);
        Assertions.assertEquals(sizeSmallest, outerList.size());
        Assertions.assertEquals(sizeBiggest, innerList.size() * outerList.size());

        Assertions.assertEquals(variables.size(), document.getElementsByTag("template").size());
    }

    @Test
    void testEach() {
        Map<String, Object> variables = new HashMap<>();
        final List<Integer> loopValues = Arrays.asList(1, 2, 3, 4, 5);
        variables.put("list-of-values", loopValues);
        InjectionResult result = TAG.inject(new InjectionResult(HTML_W_ELEMENT), variables, request);
        result = new ValueTag().inject(result, variables, request);

        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Map<String, List<Element>> templateElements = findDivsWithTemplate(Jsoup.parse(result.getHTML()));
        List<Element> divs = templateElements.get(templateElements.keySet().stream().findFirst().get());

        Assertions.assertEquals(5, divs.size());
        for(Element div : divs) {
            Assertions.assertFalse(div.text().contains("myItem"), "myItem should be replaced");
            final String eachValAsString = div.text().replace("Each value:", "").trim();
            final Integer eachVal = Integer.parseInt(eachValAsString);
            Assertions.assertTrue(loopValues.contains(eachVal));
        }
    }

    @Test
    void testMultipleEach() {
        Map<String, Object> variables = new HashMap<>();
        final List<Integer> loopValues = Arrays.asList(1, 2, 3, 4, 5);
        variables.put("list-of-values", loopValues);
        InjectionResult result = TAG.inject(new InjectionResult(HTML_W_ELEMENT), variables, request);
        result = new ValueTag().inject(result, variables, request);

        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Map<String, List<Element>> templateElements = findDivsWithTemplate(Jsoup.parse(result.getHTML()));
        List<Element> divs = templateElements.get(templateElements.keySet().stream().findFirst().get());

        Assertions.assertEquals(5, divs.size());
        for(Element div : divs) {
            Assertions.assertFalse(div.text().contains("myItem"), "myItem should be replaced");
            final String eachValAsString = div.text().replace("Each value:", "").trim();
            final Integer eachVal = Integer.parseInt(eachValAsString);
            Assertions.assertTrue(loopValues.contains(eachVal));
        }
    }

    @Test
    void testNestedEach() {
        Map<String, Object> variables = new HashMap<>();
        final List<Integer> listOfValues = Arrays.asList(1, 2, 3);
        final List<String> otherListOfValues = Arrays.asList("ABC", "DEF");
        variables.put("list-of-values", listOfValues);
        variables.put("other-list-of-values", otherListOfValues);

        InjectionResult result = TAG.inject(new InjectionResult(HTML_W_ELEMENT_NESTED), variables, request);
        result = new ValueTag().inject(result, variables, request);

        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Map<String, List<Element>> templateElements = findDivsWithTemplate(Jsoup.parse(result.getHTML()));
        List<Element> divs = new ArrayList<>();
        for(String key : templateElements.keySet()) {
            List<Element> foundElems = templateElements.get(key);
            if(foundElems.size() > divs.size()) divs = foundElems;
        }

        Assertions.assertEquals(listOfValues.size() * otherListOfValues.size(), divs.size());
        for(Element div : divs) {
            Assertions.assertFalse(div.text().contains("myItem"), "myItem should be replaced");
            Assertions.assertFalse(div.text().contains("myItem2"), "myItem2 should be replaced");
            final String eachValAsString = div.text().replace("Each value:", "").trim();
            final String[] eachValAsSplit = eachValAsString.split(" :: ");

            final Integer eachValInteger = Integer.parseInt(eachValAsSplit[0]);
            final String eachValString = eachValAsSplit[1];

            Assertions.assertTrue(listOfValues.contains(eachValInteger));
            Assertions.assertTrue(otherListOfValues.contains(eachValString));
        }
    }

    @Test
    void testNestedWithAttributeValuesEach() {
        Map<String, Object> variables = new HashMap<>();
        final List<Integer> listOfValues = Arrays.asList(1, 2, 3);
        final List<String> otherListOfValues = Arrays.asList("ABC", "DEF");
        variables.put("list-of-values", listOfValues);
        variables.put("other-list-of-values", otherListOfValues);

        InjectionResult result = TAG.inject(new InjectionResult(HTML_W_ELEMENT_NESTED_AND_VALUE_AS_ATTRIBUTE), variables, request);
        result = new ValueTag().inject(result, variables, request);

        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Map<String, List<Element>> templateElements = findDivsWithTemplate(Jsoup.parse(result.getHTML()));
        List<Element> divs = new ArrayList<>();
        for(String key : templateElements.keySet()) {
            List<Element> foundElems = templateElements.get(key);
            if(foundElems.size() > divs.size()) divs = foundElems;
        }

        Assertions.assertEquals(listOfValues.size() * otherListOfValues.size(), divs.size());
        for(Element div : divs) {
            Assertions.assertFalse(div.text().contains("myItem"), "myItem should be replaced");
            Assertions.assertFalse(div.text().contains("myItem2"), "myItem2 should be replaced");
            final String eachValAsString = div.getElementsByTag("input").val();
            Assertions.assertTrue(otherListOfValues.contains(eachValAsString));
        }
    }

    @Test
    void testNestedComplexEach() {
        Map<String, Object> variables = new HashMap<>();
        final List<ComplexObject> listOfValues = Arrays.asList(new ComplexObject("1"), new ComplexObject("2"), new ComplexObject("3"));
        final List<ComplexObject> otherListOfValues = Arrays.asList(new ComplexObject("ABC"), new ComplexObject("DEF"));
        variables.put("list-of-values", listOfValues);
        variables.put("other-list-of-values", otherListOfValues);

        InjectionResult result = TAG.inject(new InjectionResult(HTML_W_COMPLEX_ELEMENT_NESTED), variables, request);
        result = new ValueTag().inject(result, variables, request);

        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Map<String, List<Element>> templateElements = findDivsWithTemplate(Jsoup.parse(result.getHTML()));
        List<Element> divs = new ArrayList<>();
        for(String key : templateElements.keySet()) {
            List<Element> foundElems = templateElements.get(key);
            if(foundElems.size() > divs.size()) divs = foundElems;
        }

        Assertions.assertEquals(listOfValues.size() * otherListOfValues.size(), divs.size());
        for(Element div : divs) {
            Assertions.assertFalse(div.text().contains("myItem"), "myItem should be replaced");
            Assertions.assertFalse(div.text().contains("myItem2"), "myItem2 should be replaced");
            final String eachValAsString = div.text().replace("Each value:", "").trim();
            final String[] eachValAsSplit = eachValAsString.split(" :: ");

            final String eachValInteger = eachValAsSplit[0];
            final String eachValString = eachValAsSplit[1];

            Assertions.assertTrue(List.of("1", "2", "3").contains(eachValInteger));
            Assertions.assertTrue(List.of("ABC", "DEF").contains(eachValString));
        }
    }

    @Test
    void testNestedWithComplexAttributeValuesEach() {
        Map<String, Object> variables = new HashMap<>();
        final List<ComplexObject> listOfValues = Arrays.asList(new ComplexObject("1"), new ComplexObject("2"), new ComplexObject("3"));
        final List<ComplexObject> otherListOfValues = Arrays.asList(new ComplexObject("ABC"), new ComplexObject("DEF"));
        variables.put("list-of-values", listOfValues);
        variables.put("other-list-of-values", otherListOfValues);

        InjectionResult result = TAG.inject(new InjectionResult(HTML_W_ELEMENT_NESTED_AND_COMPLEX_VALUE_AS_ATTRIBUTE), variables, request);
        result = new ValueTag().inject(result, variables, request);

        System.out.println(result.getHTML());
        Assertions.assertFalse(result.getHTML().contains("m:foreach"));
        Assertions.assertTrue(result.getHTML().contains("<template") && result.getHTML().contains("</template>"));
        Map<String, List<Element>> templateElements = findDivsWithTemplate(Jsoup.parse(result.getHTML()));
        List<Element> divs = new ArrayList<>();
        for(String key : templateElements.keySet()) {
            List<Element> foundElems = templateElements.get(key);
            if(foundElems.size() > divs.size()) divs = foundElems;
        }

        Assertions.assertEquals(listOfValues.size() * otherListOfValues.size(), divs.size());
        for(Element div : divs) {
            Assertions.assertFalse(div.text().contains("myItem"), "myItem should be replaced");
            Assertions.assertFalse(div.text().contains("myItem2"), "myItem2 should be replaced");
            final String eachValAsString = div.getElementsByTag("input").val();
            Assertions.assertTrue(List.of("ABC", "DEF").contains(eachValAsString));
        }
    }

    private Map<String, List<Element>> findDivsWithTemplate(Document doc) {
        Map<String, List<Element>> result = new HashMap<>();

        Elements elementsWithTemplateId = doc.getElementsByAttribute("template-id");
        for(Element elementWithTemplateId : elementsWithTemplateId) {
            if(elementWithTemplateId.tag().getName().equals("div") && !elementWithTemplateId.parent().tag().getName().equals("template")) {
                final String templateId = elementWithTemplateId.attr("template-id");
                List<Element> elements = result.getOrDefault(templateId, new ArrayList<>());
                elements.add(elementWithTemplateId);
                result.put(templateId, elements);
            }
        }

        return result;
    }

    long countOccurrences(String block) {
        return Arrays.stream(block.split("<p>Medusa</p>")).count()-1;
    }

    class ComplexObject {

        private final String exampleValue;

        public ComplexObject(String exampleValue) {
            this.exampleValue = exampleValue;
        }

        public String getExampleValue() {
            return exampleValue;
        }
    }

}
