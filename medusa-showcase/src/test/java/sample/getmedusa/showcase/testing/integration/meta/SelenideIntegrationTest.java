package sample.getmedusa.showcase.testing.integration.meta;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.junit5.TextReportExtension;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.codeborne.selenide.Selenide.open;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({TextReportExtension.class})
public abstract class SelenideIntegrationTest {
    protected static final Logger logger = LoggerFactory.getLogger(SelenideIntegrationTest.class);
    @LocalServerPort
    private int port;

    @BeforeAll /* use application.properties to set selenide.headless=false */
    static void setup( @Value("${selenide.headless:true}") Boolean headless, @Value("${selenide.browser:chrome}") String browser) {
        switch (browser) {
            case "chrome" ->  WebDriverManager.chromedriver().setup();
            case "firefox" -> WebDriverManager.firefoxdriver().setup();
            case "edge" -> WebDriverManager.edgedriver().setup();
            default -> WebDriverManager.chromiumdriver().setup();
        }
        Configuration.headless=headless;
        Configuration.screenshots=false;
        Configuration.browser=browser;
    }

    @AfterEach
    public void close(){
        if(WebDriverRunner.isFirefox())
            WebDriverRunner.closeWindow();
    }

    protected void openPage(String page) {
        open("http://localhost:%d/detail/%s".formatted(port, page));
        Selenide.sleep(500);
    }
}
