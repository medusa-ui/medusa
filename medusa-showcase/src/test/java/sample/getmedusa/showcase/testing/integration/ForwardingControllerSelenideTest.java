package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class ForwardingControllerSelenideTest extends SelenideIntegrationTest {

    @BeforeEach
    void openPage(){
        openPage("forwarding");
    }

    @Test
    void forwardToBasicButton(){
        // when
        $(id("btn_basic-button")).click();

        // then
        $(id("counter_value")).should(exist);
    }

    @Test
    void forwardToLiveData(){
        // when
        $(id("btn_live-data")).click();

        // then
        $(className("sample-counter")).should(exist);
    }

    @Test
    void forwardRegisterMeForAForward(){
        // when
        $(id("btn_register")).click();
        sleep(700); // wait for scheduler to forward

        // then
        $(id("counter_value")).should(exist);
    }

}
