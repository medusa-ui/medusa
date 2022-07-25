package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.util.FluxUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MedusaOnEnterTest extends MedusaTagTest {

    private final String basicTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input id="cnt" m:enter="action(:{#cnt})"></div>
            </body>
            </html>
            """;

    @Test
    public void basicRenderTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(basicTemplateHTML, session));
        logger.info(template);
        Assertions.assertFalse(template.contains("th:text"), "Thymeleaf tags should be rendered");
        Assertions.assertFalse(template.contains("m:enter"), "Medusa tags should be rendered");
        Assertions.assertTrue(template.contains("onkeypress=\"if(event.key === 'Enter') { _M.doAction(null, `action('${document.querySelector('#cnt').value}')`) }\""), "Medusa tag should be rendered with replacement JS");
    }

}
