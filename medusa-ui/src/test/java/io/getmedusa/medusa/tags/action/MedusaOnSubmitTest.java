package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.util.FluxUtils;
import org.junit.jupiter.api.Test;

class MedusaOnSubmitTest extends MedusaTagTest {

    private final String basicTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <form m:submit="displayName(123, :{form}, 'sample')">
                 <label for="firstName">First name: </label> <input type="text" id="firstName" name="firstName" value="도윤" />
                 <label for="lastName">Last name: </label> <input type="text" id="lastName" name="lastName" value="김" />
                 <input type="submit" value="Submit">
                </form>
            </body>
            </html>
            """;

    @Test
    void basicRenderTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(basicTemplateHTML, session));
        System.out.println(template);
    }

}
