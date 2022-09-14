package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.selectedText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class OptionListControllerSelenideTest extends SelenideIntegrationTest {

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

        // and when
        $(id("slc_order")).selectOption("Coffee");
        $(id("slc_order")).selectOption("Hot chocolate");
        $(id("slc_drinks")).selectOption("Beers");
        $(id("slc_order")).selectOption("Dark ale");

        // then
        $$(className("order")).should(containExactTextsCaseSensitive("Coffee", "Hot chocolate", "Dark ale"));
    }
}
