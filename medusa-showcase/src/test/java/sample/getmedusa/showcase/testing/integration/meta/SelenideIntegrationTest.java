package sample.getmedusa.showcase.testing.integration.meta;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.junit5.TextReportExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.codeborne.selenide.Selenide.open;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({TextReportExtension.class})
public abstract class SelenideIntegrationTest {
    @LocalServerPort
    private int port;

    @BeforeAll /* use application.properties to set selenide.headless=false */
    static void setup( @Value("${selenide.headless:true}") Boolean headless) {
        Configuration.headless=headless;
        Configuration.screenshots=false;
        Configuration.browser="chrome";
    }

    protected void openPage(String page) {
        open("http://localhost:%d/detail/%s".formatted(port, page));
    }
}
