package sample.getmedusa.showcase.testing.integration;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;

public class UploadsControllerSelenideIntegrationTest extends SelenideIntegrationTest {

    @BeforeEach
    void setup(){
        openPage("uploads");
    }

    @Test
    void uploadFile() {
        // initial
        $(id("img_upload")).shouldNotBe(Condition.visible);
        $(id("prg_upload")).shouldNot(Condition.appear);

        // when
        $(id("single_file")).uploadFromClasspath("tree.png");
        $(id("btn_upload")).click();

        // then
        $(id("prg_upload")).shouldBe(appear);

        // and
        $(id("prg_upload")).should(disappear);

        // and
        $(id("img_upload")).shouldBe(visible);

        // upload another file
        $(id("single_file")).uploadFromClasspath("tree_2.png");
        $(id("btn_upload")).click();

        // then
        $(id("prg_upload")).shouldBe(appear);

        // and
        $(id("prg_upload")).should(disappear);

        // then
        $(id("img_upload")).shouldBe(visible);
    }
}
