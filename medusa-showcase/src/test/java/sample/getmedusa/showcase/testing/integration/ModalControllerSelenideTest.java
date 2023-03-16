package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.tagName;

public class ModalControllerSelenideTest extends SelenideIntegrationTest {

    @BeforeEach
    void setup(){
        openPage("modal");
    }

    @Test
    @DisplayName("Initially modals should be present but not visible (open)")
    void initialPage() {
        $(id("modal-1"))
                .shouldNotBe(visible)
                .shouldNotHave(cssClass("is-open"));

        $(id("modal-2"))
                .shouldNotBe(visible)
                .shouldNotHave(cssClass("is-open"));
    }

    @Test
    @DisplayName("Modals should visible (open) after a click action")
    void openModal() {
        // when
        $(id("btn_modal-1")).click();

        // then
        $(id("modal-1"))
                .shouldHave(cssClass("is-open"))
                .shouldBe(visible);
        // and when
        $(tagName("body")).sendKeys(Keys.ESCAPE);
        //then
        $(id("modal-1")).shouldNotBe(visible);

        // and when
        $(id("btn_modal-2")).click();
        //then
        $(id("modal-2"))
                .shouldBe(visible)
                .shouldHave(cssClass("is-open"));
    }
}
