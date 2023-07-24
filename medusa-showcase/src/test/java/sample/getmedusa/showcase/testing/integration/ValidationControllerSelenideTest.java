package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import java.util.Locale;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;

class ValidationControllerSelenideTest extends SelenideIntegrationTest {
    @BeforeAll
    static void setLocale() {
        Locale.setDefault(Locale.US);
    }
    @BeforeEach
    void openPage(){
        openPage("validation");
    }

    @Test
    void formValidation(){
        //no validations at start
        Assertions.assertEquals("", $("form ul").text());
        Assertions.assertEquals("", $("p[validation='email']").text());

        //no data input, simple push
        $(id("btn_displayForm")).click();

        //validate error message
        Assertions.assertEquals("""
                            email is not a valid email
                            yearOfBirth must not be blank
                            yearOfBirth fails the expected pattern""", $("form ul").text());
        Assertions.assertEquals("email is not a valid email", $("p[validation='email']").text());

        //correct data
        $(id("email")).setValue("john.doe@my-email.com");
        $(id("yearOfBirth")).setValue("1975");
        $(id("btn_displayForm")).click();

        //validate validations are ok now
        Assertions.assertEquals("", $("form ul").text());
        Assertions.assertEquals("", $("p[validation='email']").text());

        $$(className("example-result")).first().shouldBe( exactText("john.doe@my-email.com 1975"));
    }

    @Test
    void customMessage(){
        //no validations at start
        Assertions.assertEquals("", $("p[validation='email2']").text());

        //no data input, simple push
        $(id("btn_displayForm_custom_message")).click();

        //validate error message
        Assertions.assertEquals("Gelieve een geldig e-mail in te vullen", $("p[validation='email2']").text());

        //correct data
        $(id("email_custom_message")).setValue("도윤@my-email.com");
        $(id("btn_displayForm_custom_message")).click();

        //validate validations are ok now
        Assertions.assertEquals("", $("p[validation='email2']").text());

        $$(className("example-result")).last().shouldBe( exactText("도윤"));
    }
}
