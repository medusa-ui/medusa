package com.sample.medusa.meta;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.Optional;

public class AbstractSeleniumTest {

    @LocalServerPort
    private int port;

    protected WebDriver driver;
    protected static String BASE;

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

    protected boolean existsById(String id) {
        return driver.findElements(By.id(id)).size() != 0;
    }

    protected void goTo(String url) {
        driver.get(BASE + url);
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
    }

    protected void pressKeyById(String id, Keys key) {
        driver.findElement(By.id(id)).sendKeys(key);
        sleep(50);
    }

    protected void clickById(String id) {
        driver.findElement(By.id(id)).click();
    }

    protected void refreshPage() {
        driver.navigate().refresh();
    }

    protected int countOccurrence(String source, String search) {
        return source.split(search).length - 1;
    }
}