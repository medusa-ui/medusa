package sample.getmedusa.showcase.testing.integration;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.selectedText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

class OptionListControllerSelenideTest extends SelenideIntegrationTest {

    @BeforeEach
    void setup(){
        openPage("option-list");
    }

    @Test
    void select(){
        // initial
        $(id("favorite-fruit")).shouldBe(exactText("Orange"));

        // when
        $(id("select-fruit")).selectOption("Lemon");
        // then
        $(id("favorite-fruit")).shouldBe(exactText("Lemon"));

        // and when
        $(id("select-fruit")).selectOption("Strawberry");
        // then
        $(id("favorite-fruit")).shouldBe(exactText("Strawberry"));
    }

    @Test
    void drinks(){
        // initial
        $(id("slc_drinks")).shouldBe(selectedText("Waters"));

        // when
        $(id("slc_drinks")).selectOption("Hot Drinks");
        // then
        $(id("slc_drinks")).shouldBe(selectedText("Hot Drinks"));

        $(By.cssSelector("#slc_order option[value='Coffee']")).should(Condition.exist);

        // and when
        $(id("slc_order")).selectOptionByValue("Coffee");
        $(id("slc_order")).selectOptionByValue("Hot chocolate");

        $(id("slc_drinks")).selectOption("Beers");

        $(By.cssSelector("#slc_order option[value='Dark ale']")).should(Condition.exist);
        $(id("slc_order")).selectOptionByValue("Dark ale");

        // then
        $$(className("order")).should(containExactTextsCaseSensitive("Coffee", "Hot chocolate", "Dark ale"));
    }
}
