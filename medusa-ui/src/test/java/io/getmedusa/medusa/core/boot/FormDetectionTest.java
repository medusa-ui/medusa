package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.attributes.Attribute;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class FormDetectionTest {

    private String html = """
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
    }

    private class TestController {
        public List<Attribute> displayName(Integer i, BigDecimal form, String s){
            return null;
        }
    }

}
