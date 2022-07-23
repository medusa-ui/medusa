package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.tags.action.MedusaOnClick;
import io.getmedusa.medusa.core.util.FluxUtils;
import io.getmedusa.medusa.core.tags.MedusaDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

class MedusaOnClickTest extends MedusaTagTest {

    @BeforeEach
    public void init() {
        this.renderer = new Renderer(Set.of(new MedusaDialect(Set.of(new MedusaOnClick()))), null);
    }

    private final String basicTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <p>Value: <span th:text="${counter}"></span></p>
                <button m:click="increment()">increment</button>
            </body>
            </html>
            """;

    private final String parameterTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <p>Value: <span th:text="${counter}"></span></p>
                <button m:click="increment(2,'some text')">increment</button>
            </body>
            </html>
            """;

    private final String attributeTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <p>Value: <span th:text="${counter}"></span></p>
                <button m:click="action(${one},${two})">do it</button>
            </body>
            </html>
            """;

    private final String elementValueTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <span id="elm_1">42</span>
                <input id="elm_2" value="some text">
                <button m:click="action(:{#elm_1}, :{#elm_2})">increment</button>
            </body>
            </html>
            """;

    private final String complexTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <span id="elm_1">42</span>
                <input id="elm_2" value="some text">
                <button m:click="action('4', 3, ${two}, :{#elm_1})">increment</button>
            </body>
            </html>
            """;

    @Test
    void basicRenderTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(basicTemplateHTML, session));
        logger.debug(template);
        Assertions.assertFalse(template.contains("th:text="), "Thymeleaf tags should be rendered");
        Assertions.assertFalse(template.contains("m:click="), "Medusa tags should be rendered");
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `increment()`)\""), "Medusa tag should be rendered with replacement JS");
    }

    @Test
    void parameterRenderTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(parameterTemplateHTML, session));
        logger.debug(template);
        Assertions.assertFalse(template.contains("th:text="), "Thymeleaf tags should be rendered");
        Assertions.assertFalse(template.contains("m:click="), "Medusa tags should be rendered");
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `increment(2,'some text')`)\""), "Medusa tag should be rendered with replacement JS");
    }

    @Test
    void attributeValueRenderTest() {
        session.setLastParameters(List.of(new Attribute("one",1),new Attribute("two",2)));
        String template = FluxUtils.dataBufferFluxToString(renderer.render(attributeTemplateHTML, session));
        logger.debug(template);
        Assertions.assertFalse(template.contains("th:text="), "Thymeleaf tags should be rendered");
        Assertions.assertFalse(template.contains("m:click="), "Medusa tags should be rendered");
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action(1,2)`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void elementValueRenderTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(elementValueTemplateHTML, session));
        logger.debug(template);
        Assertions.assertFalse(template.contains("th:text="), "Thymeleaf tags should be rendered");
        Assertions.assertFalse(template.contains("m:click="), "Medusa tags should be rendered");
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('${document.querySelector('#elm_1').value}', '${document.querySelector('#elm_2').value}')`)\""), "Medusa tag should be able to find element values");
    }
    @Test
    void complexValueRenderTest() {
        session.setLastParameters(List.of(new Attribute("one",1),new Attribute("two",2)));
        String template = FluxUtils.dataBufferFluxToString(renderer.render(complexTemplateHTML, session));
        logger.debug(template);
        Assertions.assertFalse(template.contains("th:text="), "Thymeleaf tags should be rendered");
        Assertions.assertFalse(template.contains("m:click="), "Medusa tags should be rendered");
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('4', 3, 2, '${document.querySelector('#elm_1').value}')`)\""), "Medusa tag should be able to find element values");
    }
}
