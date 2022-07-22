package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.util.FluxUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MedusaOnChangeTest extends MedusaTagTest {

    private final String basicTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input id="cnt" m:change="action(:{#cnt})" value="42"></div>
            </body>
            </html>
            """;

    private final String selectTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input id="cnt" m:change="action(:{#cnt})" value="42"></div>
            </body>
            </html>
            """;

    @Test
    void testRender() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(basicTemplateHTML, session));
        System.out.println(template);
        Assertions.assertFalse(template.contains("th:text"), "Thymeleaf tags should be rendered");
        Assertions.assertFalse(template.contains("m:change"), "Medusa tags should be rendered");
        Assertions.assertTrue(template.contains("onchange=\"_M.doAction(null, `action('${document.querySelector('#cnt').value}')`)\""), "Medusa tag should be rendered with replacement JS");
    }

}
