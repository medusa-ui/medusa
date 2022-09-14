package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class FormSubmitControllerSelenideTest extends SelenideIntegrationTest {

    @BeforeEach
    void openPage(){
        openPage("form-submit");
    }

    @Test
    void displayName(){
        //initial
        $(id("firstName")).shouldBe(value("도윤"));
        $(id("lastName")).shouldBe(value("김"));

        //when
        $(id("btn_displayName")).click();
        //then
        $$(className("example-result")).first().shouldBe( exactText("도윤 김"));
    }

    @Test
    void displayNameAsForm(){
        //initial
        $(id("firstName2")).shouldBe(value("hello"));
        $(id("lastName2")).shouldBe(value("world"));

        //when
        $(id("btn_displayNameAsForm")).click();
        //then
        $$(className("example-result")).last().shouldBe(exactText("hello world"));

        //and when
        $(id("firstName2")).setValue("Medusa");
        $(id("lastName2")).setValue("Selenide");
        $(id("btn_displayNameAsForm")).click();
        //then
        $$(className("example-result")).last().shouldBe(exactText("Medusa Selenide"));
    }
}
