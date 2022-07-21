package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MedusaOnChangeTest extends MedusaTagTest {

    private final String basicTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <p>Value: <span id="cnt" th:text="${counter}">15</span></p>
                <div><input m:change="action(#this)" value="42"></div>
                <div><input m:change="action(#cnt)"></div>
            </body>
            </html>
            """;

    private final String selectTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input m:change="action(#this)" value="42"></div>
            </body>
            </html>
            """;

    @Test
    void testRender() {
        session.setLastParameters(List.of(new Attribute("counter", 13))); // <--- TODO test if counter is set
        String template = FluxUtils.dataBufferFluxToString(renderer.render(basicTemplateHTML, session));
        System.out.println(template);
        Assertions.assertFalse(template.contains("th:text"), "Thymeleaf tags should be rendered");
        Assertions.assertFalse(template.contains("m:change"), "Medusa tags should be rendered");
        Assertions.assertTrue(template.contains("onchange=\"_M.doAction(null, 'action(42)')\""), "Medusa tag should be rendered with replacement JS");
    }

}
