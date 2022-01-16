package io.getmedusa.medusa.core;

import io.getmedusa.medusa.core.injector.DOMChanges.DOMChange;
import io.getmedusa.medusa.core.util.ExpressionEval;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SpelExpressionTest {

    @Autowired SpelController myController;

    @Test
    void evalCheck() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("complex-object", new ComplexObject(new SubObject("x"), 5));
        assertEquals("x", ExpressionEval.evalItemAsString("complex-object.product.name", modelMap));
    }

    @Test
    void evalMapCheck() {
        Map<String, Object> modelMap = new HashMap<>();
        Map<String, String> aMap = new HashMap<>();
        aMap.put("key23", "value42");
        modelMap.put("a-map", aMap);
        assertEquals("value42", ExpressionEval.evalItemAsString("a-map['key23']", modelMap));
        assertEquals("value42", ExpressionEval.evalItemAsString("a-map[\"key23\"]", modelMap));
    }

    @Test
    void evalMapIndirectCheck() {
        Map<String, Object> modelMap = new HashMap<>();
        Map<String, String> aMap = new HashMap<>();
        aMap.put("key23", "value42");
        modelMap.put("a-map", aMap);
        modelMap.put("key-object", "key23");
        assertEquals("value42", ExpressionEval.evalItemAsString("a-map[key-object]", modelMap));
    }

    @Test
    void evalArrayCheck() {
        Map<String, Object> modelMap = new HashMap<>();
        int[] anArray = { 3, 4, 5 };
        modelMap.put("an-array", anArray);
        assertEquals("4", ExpressionEval.evalItemAsString("an-array[1]", modelMap));
    }

    @Test
    void evalArrayIndirectCheck() {
        Map<String, Object> modelMap = new HashMap<>();
        int[] anArray = { 3, 4, 5 };
        modelMap.put("an-array", anArray);
        modelMap.put("index", 1);
        assertEquals("4", ExpressionEval.evalItemAsString("an-array[index]", modelMap));
    }

    @Test
    void evalNestedMap() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("map2", Map.of("y", "123"));
        modelMap.put("map", Map.of("x", "y"));
        modelMap.put("key", "x");
        System.out.println(ExpressionEval.evalItemAsString("map2[map[key]]", modelMap));
        assertEquals("123", ExpressionEval.evalItemAsString("map2[map[key]]", modelMap));
    }

    @Test
    void evalManyMaps() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("person", new SubObject("y"));
        modelMap.put("bigmap", Map.of("x", Map.of("y", "123")));
        modelMap.put("key", "x");
        System.out.println(ExpressionEval.evalItemAsString("bigmap[key][person.name]", modelMap));
        assertEquals("123", ExpressionEval.evalItemAsString("bigmap[key][person.name]", modelMap));
    }

    @Test
    void ctrlSay() {
        // given
        String tag = "<button m:click='say(\"Hallo World\", 3)' /> ";
        SpelExpressionParser parser = new SpelExpressionParser();

        // when
        List<DOMChange> list = (List<DOMChange>) parser.parseExpression(fakeParsingTag(tag)).getValue(myController);
        System.out.println(list.get(0).getV());

        // then
        assertEquals("Hallo World, Hallo World, Hallo World", list.get(0).getV());
    }

    String fakeParsingTag(String tag){
        return tag.split("'")[1];
    }

}

@Component
class SpelController {

    public List<DOMChange> say(String message, Integer times) {
        StringBuilder sb = new StringBuilder();
        String appender = "";
        for (int i = 0; i < times; i++) {
            sb.append(appender);
            sb.append(message );
            appender = ", ";
        }
        return Collections.singletonList(new DOMChange("message", sb.toString()));
    }

}


class SubObject {
    String name;

    public SubObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class ComplexObject {
    String id;
    SubObject product;
    Integer number;

    public ComplexObject(SubObject product, Integer number) {
        this.id = UUID.randomUUID().toString();
        this.product = product;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public SubObject getProduct() {
        return product;
    }
}
