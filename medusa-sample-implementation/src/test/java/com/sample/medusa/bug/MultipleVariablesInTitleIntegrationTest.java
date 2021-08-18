package com.sample.medusa.bug;

import com.sample.medusa.meta.AbstractSeleniumTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MultipleVariablesInTitleIntegrationTest extends AbstractSeleniumTest {

    @Test
    void testTwoVariablesInTitleSwappingPlace() {
        goTo("/test/bug/multiple-variable-in-title");

        Assertions.assertEquals("Medusa in other languages : 메두사 美杜莎", title());

        clickById("swap");

        Assertions.assertEquals("Medusa in other languages : 美杜莎 메두사", title());
    }

    @Test
    void testTwoVariablesInTitleOnlyOneChanges() {
        goTo("/test/bug/multiple-variable-in-title");

        Assertions.assertEquals("Medusa in other languages : 메두사 美杜莎", title());

        clickById("changeVarTwoToXYZ");

        Assertions.assertEquals("Medusa in other languages : 메두사 XYZ", title());
    }
}
