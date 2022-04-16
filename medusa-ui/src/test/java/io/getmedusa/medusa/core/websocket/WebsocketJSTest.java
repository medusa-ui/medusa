package io.getmedusa.medusa.core.websocket;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

class WebsocketJSTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor javascriptExecutor;

    private boolean isHeadless() {
        return true;
    }

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setupTest() {
        final ChromeOptions chromeOptions = new ChromeOptions();
        if (isHeadless()) {
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("disable-gpu");
            chromeOptions.addArguments("window-size=1400,2100");
        }
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        wait = new WebDriverWait(driver,5);
        javascriptExecutor = (JavascriptExecutor) driver;
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
    protected String loadPage() {
        Resource testFile = new ClassPathResource("test.html");

        String url;
        try {
            url = testFile.getURL().toString();
        } catch (IOException e) { throw new RuntimeException(e); }

        driver.get(url);
        waitUntilPageLoaded();
        return driver.getWindowHandle();
    }

    private void waitUntilPageLoaded() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    protected String getClass(String id) {
        final By by = By.id(id);
        waitUntilElementsPresent(by);
        return driver.findElement(by).getAttribute("class");
    }

    protected String getText(String id) {
        final By by = By.id(id);
        waitUntilElementsPresent(by);
        return driver.findElement(by).getText();
    }

    protected List<WebElement> getCss(String css) {
        final By by = By.cssSelector(css);
        return driver.findElements(by);
    }

    private void waitUntilElementsPresent(By by) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    private final static String PASS = "qunit-pass";

    @Test
    void testJavascript() {
        loadPage();
        System.out.println(getText("qunit-testresult-display"));

        StringBuilder sb = new StringBuilder("\nFailing JS unit test: ");
        List<WebElement> failingTests = getCss("#qunit-tests > li.fail > strong");
        if(!failingTests.isEmpty()) {
            for(WebElement failingTest : failingTests) {
                sb.append("\n");
                sb.append(failingTest.getText());
            }
        }

        Assertions.assertEquals(0, failingTests.size(), sb.toString());
        Assertions.assertEquals(PASS, getClass("qunit-banner"));
    }

}
