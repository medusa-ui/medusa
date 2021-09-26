package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NestedEachEventIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testRendering() {
        goTo("/nested-each");

        /*
         * [$foreach $outer (1,2,3)]
            <div class="outer-1">[$each]</div> = 1, 2, 3
            <div class="outer-2">[$this.each]</div> = 1, 2, 3
            [$foreach $mid (10,20)]
                <div class="mid-1">[$this.each]</div> = 10, 20, 10, 20, 10, 20
                <div class="mid-1">[$each]</div> = 10, 20, 10, 20, 10, 20
                <div class="mid-2">[$parent.each]</div> = 1, 1, 2, 2, 3, 3
                [$foreach $inner (100)]
                    <div class="inner-1">[$this.each]</div> 100, 100, 100, 100, 100, 100
                    <div class="inner-2">[$parent.each]</div> 10, 20, 10, 20, 10, 20
                    <div class="inner-3">[$parent.parent.each]</div> 1, 1, 2, 2, 3, 3
                [$end for]
            [$end for]
        [$end for]
         */

        Assertions.assertEquals("Medusa nested each", driver.getTitle());
        Assertions.assertEquals(toList(1, 2, 3), getTextByCss(".outer-1").toString());
        Assertions.assertEquals(toList(1, 2, 3), getTextByCss(".outer-2").toString());

        Assertions.assertEquals(toList(10, 10, 20, 20, 10, 10, 20, 20, 10, 10, 20, 20), getTextByCss(".mid-1").toString());
        Assertions.assertEquals(toList(1, 1, 2, 2, 3, 3), getTextByCss(".mid-2").toString());

        Assertions.assertEquals(toList(100, 100, 100, 100, 100, 100), getTextByCss(".inner-1").toString());
        Assertions.assertEquals(toList(10, 20, 10, 20, 10, 20), getTextByCss(".inner-2").toString());
        Assertions.assertEquals(toList(1, 1, 2, 2, 3, 3), getTextByCss(".inner-3").toString());
    }

    @Test
    void testParentEach() {
        goTo("/nested-each");

        Assertions.assertEquals("Medusa nested each", driver.getTitle());
        clickById("change-mid");

        Assertions.assertEquals(toList(4, 5, 6), getTextByCss(".outer-1").toString());
        Assertions.assertEquals(toList(4, 5, 6), getTextByCss(".outer-2").toString());

        Assertions.assertEquals(toList(11, 11, 21, 21, 11, 11, 21, 21, 11, 11, 21, 21), getTextByCss(".mid-1").toString());
        Assertions.assertEquals(toList(4, 4, 5, 5, 6, 6), getTextByCss(".mid-2").toString());

        Assertions.assertEquals(toList(100, 100, 100, 100, 100, 100), getTextByCss(".inner-1").toString());
        Assertions.assertEquals(toList(11, 21, 11, 21, 11, 21), getTextByCss(".inner-2").toString());
        Assertions.assertEquals(toList(4, 4, 5, 5, 6, 6), getTextByCss(".inner-3").toString());
    }

    private String toList(Integer ... i) {
        return Arrays.asList(i).toString();
    }

}
