package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

public class MultiSelectControllerSelenideTest extends SelenideIntegrationTest {

    @BeforeEach
    void setup(){
        openPage("multi-select");
    }

    @Test
    @DisplayName("select multiple values with select[multiple]")
    void favoriteFruitsMulti(){
        // when
        $(id("m_select")).selectOption("Lemon", "Strawberry");
        $(id("m_submit")).click();

        // then
        $$(className("example-result")).first()
                .shouldHave(text("Lemon"))
                .shouldHave(text("Strawberry"))
                .shouldNotHave(text("Orange"));
    }

    @Test
    @DisplayName("select single value with select[multiple]")
    void favoriteFruitsSingle(){
        // when
        $(id("m_select")).selectOption( "Banana");
        $(id("m_submit")).click();

        // then
        $$(className("example-result")).first()
                .shouldHave(text("Banana"))
                .shouldNotHave(text("Strawberry"))
                .shouldNotHave(text("Lemon"));
    }

    @Test
    @DisplayName("select multiple values with checkboxes")
    void favoritesMultiple(){
        // when
        $$(("input[name='favoriteFruits']")).get(3).click(); // Orange
        $$(("input[name='favoriteFruits']")).get(1).click(); // Banana
        $(id("submit_form")).click();

        // then
        $$(className("example-result")).last()
                .shouldHave(text("Banana"))
                .shouldHave(text("Orange"))
                .shouldNotHave(text("Apple"))
                .shouldNotHave(text("Strawberry"));
    }

    @Test
    @DisplayName("select single value with checkboxes")
    void favoritesSingle(){
        // when
        $$(("input[name='favoriteFruits']")).get(3).click(); // Orange
        $(id("submit_form")).click();

        // then
        $$(className("example-result")).last()
                .shouldNotHave(text("Banana"))
                .shouldHave(text("Orange"))
                .shouldNotHave(text("Apple"))
                .shouldNotHave(text("Strawberry"));
    }
}
