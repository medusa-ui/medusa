package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;

public class BasicButtonControllerSelenideTest extends SelenideIntegrationTest {
    
    @BeforeEach
    void openPage(){
        openPage("basic-button");
    }

    @Test
    void updateCounter(){
        // initial
        $(id("counter_value")).shouldBe(exactText("0"));

        // when
        $(id("btn_update")).click();

        //then
        $(id("counter_value")).shouldBe(exactText("1"));

        // and when
        $(id("btn_update")).click();
        $(id("counter_value")).shouldBe(exactText("2"));
    }

    @Test
    void resetCounter(){
        // initial
        $(id("counter_value")).shouldBe(exactText("0"));

        // when
        $(id("btn_reset")).click();
        $(id("counter_value")).shouldBe(exactText("0"));

        // and when
        $(id("btn_update")).click();
        $(id("counter_value")).shouldBe(exactText("1"));
        $(id("btn_reset")).click();
        $(id("counter_value")).shouldBe(exactText("0"));

    }

}
