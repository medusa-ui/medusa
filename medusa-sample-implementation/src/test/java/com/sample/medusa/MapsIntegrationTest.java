package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MapsIntegrationTest extends AbstractSeleniumTest {

    @Test
    public void testRendering() {
        goTo("/maps");

        assertEquals("Medusa using HashMaps and Arrays", driver.getTitle());
        assertEquals(List.of("John"), getTextByCss(".person-name"));
        assertEquals(List.of("1"), getTextByCss(".map-singleq-a"));
        assertEquals(List.of("1"), getTextByCss(".map-doubleq-a"));
        assertEquals(List.of("a1"), getTextByCss(".array-0"));

        List<String> actual = getTextByCss(".map-foreach");
        assertTrue(actual.contains("a - 1\nb - 2") || actual.contains("b - 2\na - 1"));

        assertEquals(List.of("a1\nb2"), getTextByCss(".array-foreach"));
    }

    @Test
    public void testUpdated() {
        goTo("/maps");

        assertEquals("Medusa using HashMaps and Arrays", driver.getTitle());

        clickById("change-person");
        assertEquals(List.of("Paul"), getTextByCss(".person-name"));

        clickById("change-map");
        assertEquals(List.of("5"), getTextByCss(".map-singleq-a"));
        assertEquals(List.of("5"), getTextByCss(".map-doubleq-a"));

        // TODO: address: foreach loops iterating over a map don't seem to update
        /*
        List<String> actual = getTextByCss(".map-foreach");
        assertTrue(actual.contains("a - 5\nb - 6") || actual.contains("b - 6\na - 5"));
        */

        clickById("change-array");
        assertEquals(List.of("a5"), getTextByCss(".array-0"));
        assertEquals(List.of("a5\nb6"), getTextByCss(".array-foreach"));
    }

}
