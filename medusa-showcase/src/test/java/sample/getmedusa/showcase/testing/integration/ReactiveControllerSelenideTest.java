package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;

class ReactiveControllerSelenideTest extends SelenideIntegrationTest {

    @BeforeEach
    void openPage(){
        openPage("reactive");
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
}
