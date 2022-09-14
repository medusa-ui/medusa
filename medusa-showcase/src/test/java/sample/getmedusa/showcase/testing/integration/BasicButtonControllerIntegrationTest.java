package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicButtonControllerIntegrationTest extends SelenideTest {

    private static String PAGE = "/detail/basic-button";

    @BeforeEach
    void openPage(){
        openPage(PAGE);
    }

    @Test
    void updateCounter(){
        // initial
        assertEquals( "0", getTextById("counter_value"),"The initial value of the counter should be 0.");

        // when
        clickById("btn_update");

        //then
        assertEquals( "1", getTextById("counter_value"), "The value of the counter should increased by 1.");

        // and when
        clickById("btn_update");
        // then
        assertEquals("2", getTextById("counter_value"),"The value of the counter should  be 2 now.");
    }

    @Test
    void resetCounter(){
        // initial
        assertEquals( "0", getTextById("counter_value"), "The initial value of the counter should be 0.");

        // when
        clickById("btn_reset");

        //then
        assertEquals( "0", getTextById("counter_value"),"The value of the counter should not still be 0");

        // and when
        clickById("btn_update");
        // then
        assertEquals("1", getTextById("counter_value"), "The value of the counter should increased by 1");

        // and when
        clickById("btn_reset");
        // then
        assertEquals("0", getTextById("counter_value"),"The value of the counter should reset to 0");
    }

}
