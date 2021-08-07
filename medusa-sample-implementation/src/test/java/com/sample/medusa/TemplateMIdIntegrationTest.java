package com.sample.medusa;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TemplateMIdIntegrationTest extends AbstractSeleniumTest {

    private static final String page = "/test/history";
    protected static final String TEST_TEXT = "Hello World";

    @Override
    protected boolean isHeadless() {
        return false;
    }

    @Test
    @DisplayName("Using template with m-id should allow the usage of identical foreach-blocks")
    void test() {
        final String mId = testInitPage();
        addHistoricEventAndVerifyParagraphCount(2, mId);
        addHistoricEventAndVerifyParagraphCount(4, mId);
        refreshPage();
        addHistoricEventAndVerifyParagraphCount(6, mId);
    }

    String testInitPage() {
        goTo(page);

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("History has a tendency"));

        List<String> mIdsUsed = getAttributeByCss("template", "m-id");
        Assertions.assertEquals(2, mIdsUsed.size());
        Assertions.assertEquals(1, new HashSet<>(mIdsUsed).size());
        return mIdsUsed.get(0);
    }

    void addHistoricEventAndVerifyParagraphCount(int expectedParagraphCount, String mId) {
        fillFieldById("input-event", TEST_TEXT);
        pressKeyById("input-event", Keys.ENTER);

        List<String> textInParagraphs = getTextByCss("div[template-id='"+mId+"'] p");
        Assertions.assertEquals(expectedParagraphCount, textInParagraphs.size());
        verifySameTextInAllParagraphs(textInParagraphs);
    }

    private void verifySameTextInAllParagraphs(List<String> textInParagraphs) {
        final Set<String> setOfTextInParagraphs = new HashSet<>(textInParagraphs);
        Assertions.assertEquals(1, setOfTextInParagraphs.size());
        Assertions.assertEquals(TEST_TEXT, setOfTextInParagraphs.iterator().next());
    }
}
