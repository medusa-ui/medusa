package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;


public class LiveDataControllerSelenideTest extends SelenideIntegrationTest {

    @BeforeEach
    void loadPage(){
        openPage("live-data");
    }

    @Test
    void globalLiveData(){
        // wait for a random number
        sleep(750);
        // then
        String current = $(className("global")).text();
        sleep(750); // wait for a new number
        String newValue = $(className("global")).text();

        logger.info(current + " != " + newValue);
        Assertions.assertNotEquals(current, newValue);
    }

    @Test
    void sessionLiveData(){
        // wait for initial random number to be generated
        sleep(1250);
        String initValue = $(className("unique")).text();
        // when
        sleep(1250);
        String valueAfterPause = $(className("unique")).text();
        // then
        logger.info(initValue + " =? " + valueAfterPause);
        Assertions.assertEquals(initValue,valueAfterPause);

        // and when
        $(id("btn_unique")).click();
        sleep(1250);
        // then
        String newValue = $(className("unique")).text();
        logger.info(initValue + " =? " + newValue);
        Assertions.assertNotEquals(initValue, newValue);
    }

    @Test
    void groupLiveData(){
        // wait for initial random number to be generated
        sleep(1250);
        String initValue = $(className("group")).text();
        // when
        sleep(1250);
        String valueAfterPause = $(className("group")).text();
        // then
        logger.info(initValue + " =? " + valueAfterPause);
        Assertions.assertEquals(initValue, valueAfterPause);

        // and when
        $(id("btn_group")).click();
        sleep(1250);
        // then
        String newValue = $(className("group")).text();
        logger.info(initValue + " =? " + newValue);
        Assertions.assertNotEquals(initValue,newValue);
    }
}
