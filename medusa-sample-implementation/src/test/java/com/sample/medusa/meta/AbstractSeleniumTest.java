package com.sample.medusa.meta;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AbstractSeleniumTest {

    @LocalServerPort
    private int port;

    protected WebDriver driver;
    protected static String BASE;
    protected WebDriverWait wait;

    protected boolean isHeadless() {
        return true;
    }

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setupTest() {
        BASE = "http://localhost:" + port;
        final ChromeOptions chromeOptions = new ChromeOptions();
        if (isHeadless()) {
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("disable-gpu");
            chromeOptions.addArguments("window-size=1400,2100");
        }
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver,1);
        login();
    }

    private void login() {
        driver.get(BASE);
        if(existsById("username")) {
            fillFieldById("username", "user");
            fillFieldById("password", "password");
            clickByCss(".btn-block");
        }
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected String getFromValue(String fromValue) {
        sleep(50);
        return driver.findElement(By.cssSelector("[from-value='"+fromValue+"']")).getText();
    }

    protected void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception ignored) {
        }
    }

    protected void clickByCss(String cssSelector) {
        driver.findElement(By.cssSelector(cssSelector)).click();
    }

    protected boolean existsByCss(String cssSelector) {
        return driver.findElements(By.cssSelector(cssSelector)).size() != 0;
    }

    protected List<String> getAttributeByCss(String cssSelector, String attribute) {
        waitUntilElementsPresent(cssSelector);
        return driver.findElements(By.cssSelector(cssSelector)).stream().map(w -> w.getAttribute(attribute)).collect(Collectors.toList());
    }

    protected List<String> getTextByCss(String cssSelector) {
        waitUntilElementsPresent(cssSelector);
        return driver.findElements(By.cssSelector(cssSelector)).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    private void waitUntilElementsPresent(String cssSelector) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(cssSelector)));
    }

    protected boolean existsById(String id) {
        return driver.findElements(By.id(id)).size() != 0;
    }

    protected String title() {
        return driver.getTitle();
    }

    protected void goTo(String url) {
        driver.get(BASE + url);
        waitUntilPageLoaded();
    }

    private void waitUntilPageLoaded() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    protected String getTextById(String id) {
        return driver.findElement(By.id(id)).getText();
    }

    protected String getTextByClass(String clazz) {
        final List<WebElement> elements = driver.findElements(By.className(clazz));
        final Optional<WebElement> optionalMatch = elements.stream().filter(e -> !e.getText().isEmpty()).findFirst();
        return optionalMatch.map(WebElement::getText).orElse(null);
    }

    protected void fillFieldById(String id, String keys) {
        final WebElement element = driver.findElement(By.id(id));
        element.clear();
        element.sendKeys(keys);
        sleep(50);
    }

    protected void pressKeyById(String id, Keys key) {
        final WebElement element = driver.findElement(By.id(id));
        element.sendKeys(key);
        sleep(50);
    }

    protected void clickById(String id) {
        driver.findElement(By.id(id)).click();
        sleep(50);
    }

    protected void refreshPage() {
        driver.navigate().refresh();
        waitUntilPageLoaded();
    }
}