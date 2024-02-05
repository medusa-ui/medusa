package io.getmedusa.medusa.tags;

import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.tags.MedusaDialect;
import io.getmedusa.medusa.core.tags.attribute.MedusaControllerAttribute;
import io.getmedusa.medusa.core.util.FluxUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

public class ControllerTagTest {
    private static final Logger logger = LoggerFactory.getLogger(ControllerTagTest.class);
    final Renderer renderer = new Renderer(Set.of(new MedusaDialect(Set.of(new MedusaControllerAttribute()))), null,  "self", null, null);

    @Test
    void basicRenderTest() {
        // given
        String basicTemplateHTML = """
                <!DOCTYPE html>
                <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="http://www.getmedusa.io">
                <body m:controller="example.controller.MyController">
                    <p th:text="${message}">Hello World.</p>
                </body>
                </html>
                """;
        Session session = new Session();
        session.merge($$("message","Hallo Medusa!"));

        // when
        String template = FluxUtils.dataBufferFluxToString(renderer.render(basicTemplateHTML, session));
        logger.debug(template);

        // then
        Assertions.assertTrue(template.contains("<p>Hallo Medusa!</p>"),"Tags should be resolved");

        // and
        Assertions.assertFalse(template.contains("m:controller"), "Medusa tags should be removed");
        Assertions.assertFalse(template.contains("example.controller.MyController"), "Reference to controller should not be included");;
    }
}
