package sample.getmedusa.showcase.testing.integration.meta;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
             //, properties = { "headless=false", "wait-after-click=250", "spring.rsocket.server.port=7007" }
)
public abstract class SelenideTest {

    private static final Logger logger = LoggerFactory.getLogger(SelenideTest.class);
    @LocalServerPort
    private int port;
    @Value("${headless:true}")
    protected Boolean headless;
    @Value("${wait-after-click:250}")
    protected int waitAfterClick;

    /* test if selected element is available */
    protected Condition available = and("is available", visible, enabled);

    @BeforeEach
    void setup(){
        Configuration.headless=headless;
    }

    protected void openPage(String route) {
        if (route.startsWith("/")) {
            route = "http://localhost:%d%s".formatted(port, route);
        }
        open(route);
    }

    protected void clickById(String id) {
        availableElementById(id).click();
        sleep(waitAfterClick);
        logger.info("clicked elem with id='%s'".formatted(id));
    }

    protected String getTextById(String id) {
        String text = availableElementById(id).getText();
        logger.info("getTextById('%s') = '%s'".formatted(id, text));
        return text;
    }

    protected SelenideElement availableElementById(String id){
        return $$(By.id(id)).filter(available).first();
    }

    protected List<String> getTextByCss(String selector) {
        List<String> text = availableElementByCSS(selector).stream().map(SelenideElement::getText).toList();
        logger.info("getTextById('%s') = '%s'".formatted(selector, text));
        return text;
    }

    protected void select(String id, int... indexes){
        SelenideElement selenideElement = availableElementById(id);
        selenideElement.selectOption(indexes);
    }

    protected List<SelenideElement> availableElementByCSS(String css){
        return $$(By.cssSelector(css)).filter(available);
    }
}
