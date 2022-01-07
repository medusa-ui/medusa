package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MapsIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testRendering() {
        goTo("/maps");

        assertEquals("Medusa using HashMaps and Arrays", driver.getTitle());
        assertEquals(List.of("John"), getTextByCss(".person-name"));
        assertEquals(List.of("1"), getTextByCss(".map-singleq-a"));
        assertEquals(List.of("1"), getTextByCss(".map-doubleq-a"));
        assertEquals(List.of("a1"), getTextByCss(".array-0"));

        List<String> mapForeach = getTextByCss(".map-foreach");
        Collections.sort(mapForeach);
        Assertions.assertEquals("[a - 1, b - 2]", mapForeach.toString());

        final List<String> arrayElements = getTextByCss(".array-foreach");
        Collections.sort(arrayElements);
        assertEquals("[a1, b2]", arrayElements.toString());
    }

    @Test
    void testUpdated() {
        goTo("/maps");

        assertEquals("Medusa using HashMaps and Arrays", driver.getTitle());

        clickById("change-person");
        assertEquals(List.of("Paul"), getTextByCss(".person-name"));

        clickById("change-map");
        assertEquals(List.of("5"), getTextByCss(".map-singleq-a"));
        assertEquals(List.of("5"), getTextByCss(".map-doubleq-a"));

        List<String> mapForeach = getTextByCss(".map-foreach");
        Collections.sort(mapForeach);
        Assertions.assertEquals("[a - 5, b - 6]", mapForeach.toString());

        clickById("change-array");
        assertEquals(List.of("a5"), getTextByCss(".array-0"));
        final List<String> arrayElements = getTextByCss(".array-foreach");
        Collections.sort(arrayElements);
        assertEquals("[a5, b6]", arrayElements.toString());
    }

}
