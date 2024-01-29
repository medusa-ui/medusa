package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class FormDetectionTest {

    private final String html = """
            <form m:submit="displayName(123, :{form}, 'sample')">
                <label for="firstName">First name: </label>
                <input type="text" id="firstName" name="firstName" />
                        
                <label for="lastName">Last name: </label>
                <input type="text" id="lastName" name="lastName" />
                        
                <input type="submit" value="Submit">
            </form>
                        
            <div class="example-result" th:text="${result}"></div>
            """;

    @Test
    void testParsing() {
        FormDetection.INSTANCE.prepFile(html, new TestController());
        Assertions.assertEquals(FormDetection.INSTANCE.getFormClass(TestController.class.getName(), "displayName"), BigDecimal.class);
    }

    @Test
    void testParsingWithSession() {
        FormDetection.INSTANCE.prepFile(html, new SessionTestController());
        Assertions.assertEquals(FormDetection.INSTANCE.getFormClass(SessionTestController.class.getName(), "displayName"), BigDecimal.class);
    }

    private static class TestController {
        public List<Attribute> displayName(Integer i, BigDecimal form, String s){
            return null;
        }
    }

    private static class SessionTestController {
        public List<Attribute> displayName(Session session, Integer i, BigDecimal form, String s){
            return null;
        }
    }
}
