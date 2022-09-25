package sample.getmedusa.showcase.testing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.getmedusa.showcase.testing.integration.meta.SelenideIntegrationTest;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.className;

public class FragmentControllerSelenideTest extends SelenideIntegrationTest {

    @BeforeEach
    void openPage(){
        openPage("fragments");
    }

    @Test
    void fragments(){
        //fragments should be present on page
        $$(className("sample-ref")).should(size(2));
        $$(className("fallback-ref")).should(size(1));
    }
}
