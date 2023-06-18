package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class JSHooksControllerSelenideTest extends SelenideIntegrationTest {

    @BeforeEach
    void openPage(){
        openPage("js-hooks");
    }

    @Test
    void pressCount() {
        // initial page
        $(By.id("pre-render")).shouldHave(exactText("No pre-render call"));
        $(By.id("pre-event")).shouldHave(exactText("No pre-event call"));
        $(By.id("post-event")).shouldHave(exactText("No post-event call"));
        $(By.id("post-render")).shouldHave(exactText("No post-render call"));

        // when
        $(By.id("btn_update")).click();
        // then
        $(By.id("pre-render")).shouldHave(text("\"content\":\"1\""));
        $(By.id("pre-event")).shouldHave(text("\"content\":\"1\""));
        $(By.id("post-event")).shouldHave(text("\"content\":\"1\""));
        $(By.id("post-render")).shouldHave(text("\"content\":\"1\""));
        // and when
        $(By.id("btn_update")).click();
        // then
        $(By.id("pre-render")).shouldHave(text("\"content\":\"2\""));
        $(By.id("pre-event")).shouldHave(text("\"content\":\"2\""));
        $(By.id("post-event")).shouldHave(text("\"content\":\"2\""));
        $(By.id("post-render")).shouldHave(text("\"content\":\"2\""));
    }

    @Test
    void pressReset() {
        // initial page
        $(By.id("pre-render")).shouldHave(exactText("No pre-render call"));
        $(By.id("pre-event")).shouldHave(exactText("No pre-event call"));
        $(By.id("post-event")).shouldHave(exactText("No post-event call"));
        $(By.id("post-render")).shouldHave(exactText("No post-render call"));
        // when
        $(By.id("btn_reset")).click();
        // then
        $(By.id("pre-render")).shouldHave(exactText("[]"));
        $(By.id("pre-event")).shouldHave(exactText("No pre-event call"));
        $(By.id("post-event")).shouldHave(exactText("No post-event call"));
        $(By.id("post-render")).shouldHave(exactText("[]"));

        // when
        $(By.id("btn_reset")).click();
        // then
        $(By.id("pre-render")).shouldHave(exactText("[]"));
        $(By.id("pre-event")).shouldHave(exactText("No pre-event call"));
        $(By.id("post-event")).shouldHave(exactText("No post-event call"));
        $(By.id("post-render")).shouldHave(exactText("[]"));

        // and when count + reset
        $(By.id("btn_update")).click();
        // then
        $(By.id("pre-render")).shouldHave(text("\"content\":\"1\""));
        $(By.id("pre-event")).shouldHave(text("\"content\":\"1\""));
        $(By.id("post-event")).shouldHave(text("\"content\":\"1\""));
        $(By.id("post-render")).shouldHave(text("\"content\":\"1\""));
        // and when
        $(By.id("btn_reset")).click();
        // then
        $(By.id("pre-render")).shouldHave(text("\"content\":\"0\""));
        $(By.id("pre-event")).shouldHave(text("\"content\":\"0\""));
        $(By.id("post-event")).shouldHave(text("\"content\":\"0\""));
        $(By.id("post-render")).shouldHave(text("\"content\":\"0\""));
    }
}
