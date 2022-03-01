package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TableIterationIntegrationTest extends AbstractSeleniumTest {

    static List<String> itemNames= List.of("zero", "one", "two", "three");
    static List<String> itemValues= List.of("nul", "één", "twee", "drie");

    static String clearBtnId ="clear_btn";
    static String loadBtnId ="load_btn";
    static String addBtnId ="add_btn";

    @Test
    @DisplayName("initial loaded table-data")
    void loadedPage(){
        goTo("/test/bug/table?load=true");

        List<String> plainItemNames = getAllTextByClass("plain-item-name");
        List<String> tableItemNames = getAllTextByClass("table-item-name");
        List<String> plainItemValues = getAllTextByClass("plain-item-value");
        List<String> tableItemValues = getAllTextByClass("table-item-value");

        // plain
        Assertions.assertEquals(4, plainItemNames.size());
        Assertions.assertEquals(itemValues, plainItemValues);
        Assertions.assertEquals(itemNames, plainItemNames);

        // table
        Assertions.assertEquals(4, tableItemNames.size());
        Assertions.assertEquals(itemNames, tableItemNames);
        Assertions.assertEquals(itemValues, tableItemValues);
    }

    @Test
    @DisplayName("initial empty table-data")
    void emptyPage(){
        goTo("/test/bug/table?load=false");

        Assertions.assertEquals(0, getAllTextByClass("plain-item-name").size());
        Assertions.assertEquals(0, getAllTextByClass("table-item-name").size());

        Assertions.assertEquals(0, getAllTextByClass("plain-item-value").size());
        Assertions.assertEquals(0, getAllTextByClass("table-item-value").size());
    }

    @Test
    @DisplayName("plain-data after DOMChanges")
    void plainDOMChanges() {
        goTo("/test/bug/table?load=true");

        //clear data
        clickById(clearBtnId);
        Assertions.assertEquals(0, getAllTextByClass("plain-item-name").size());
        Assertions.assertEquals(0, getAllTextByClass("plain-item-value").size());

        //load data
        clickById(loadBtnId);
        Assertions.assertEquals(4, getAllTextByClass("plain-item-name").size());
        Assertions.assertEquals(4, getAllTextByClass("plain-item-value").size());
        Assertions.assertEquals(itemNames, getAllTextByClass("plain-item-name"));
        Assertions.assertEquals(itemValues, getAllTextByClass("plain-item-value"));

        //add data
        clickById(addBtnId);
        Assertions.assertEquals(5, getAllTextByClass("plain-item-name").size());
        Assertions.assertEquals(5, getAllTextByClass("plain-item-value").size());
        Assertions.assertTrue(getAllTextByClass("plain-item-name").contains("four"));
        Assertions.assertTrue(getAllTextByClass("plain-item-value").contains("vier"));
    }

    @Test
    @DisplayName("table-data after DOMChanges")
    void tableDOMChanges() {
        goTo("/test/bug/table?load=true");

        //clear data
        clickById(clearBtnId);
        Assertions.assertEquals(0, getAllTextByClass("table-item-name").size());
        Assertions.assertEquals(0, getAllTextByClass("table-item-value").size());

        //load data
        clickById(loadBtnId);
        Assertions.assertEquals(4, getAllTextByClass("table-item-name").size());
        Assertions.assertEquals(4, getAllTextByClass("table-item-value").size());
        Assertions.assertEquals(itemNames, getAllTextByClass("table-item-name"));
        Assertions.assertEquals(itemValues, getAllTextByClass("table-item-value"));

        //add data
        clickById(addBtnId);
        Assertions.assertEquals(5, getAllTextByClass("table-item-name").size());
        Assertions.assertEquals(5, getAllTextByClass("table-item-value").size());
        Assertions.assertTrue(getAllTextByClass("table-item-name").contains("four"));
        Assertions.assertTrue(getAllTextByClass("table-item-value").contains("vier"));
    }
}
