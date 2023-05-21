package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.tags.MedusaDialect;
import io.getmedusa.medusa.core.tags.action.MedusaValidate;
import io.getmedusa.medusa.core.util.FluxUtils;
import io.getmedusa.medusa.core.validation.ValidationMessageResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class MedusaValidateTest extends MedusaTagTest {

    @BeforeEach
    public void init() {
        this.renderer = new Renderer(Set.of(new MedusaDialect(Set.of(new MedusaValidate()))), null, "self", new ValidationMessageResolver(null), null);
    }

    private final String basicTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <form m:submit="doNoValidationForm(:{form})" class="wrapper">
                    <h1>No validation check</h1>
                    <ul m:validation="all"></ul>
                    <p><input type="text" name="value123" /></p>
                    <p m:validation="value123"></p>
            
                    <input type="submit" value="Submit">
                </form>
            </body>
            </html>
            """;

    private final String withClassesHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <form m:submit="doNoValidationForm(:{form})" class="wrapper">
                    <h1>No validation check</h1>
                    <ul class="test321 red" m:validation="all"></ul>
                    <p><input type="text" name="value123" /></p>
                    <p class="test123 orange" m:validation="value123"></p>
            
                    <input type="submit" value="Submit">
                </form>
            </body>
            </html>
            """;

    //- replace m:validation="all" tag with class="error" validation="form-global", careful if it already has a class
    //- replace m:validation="value" tag with class="error hidden" validation="value"

    @Test
    void basicRenderTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(basicTemplateHTML, session));
        logger.debug(template);
        Assertions.assertFalse(template.contains("m:validation="), "Thymeleaf tags should be rendered");
        Assertions.assertTrue(template.contains("validation=\"form-global\""), "Medusa tag m:validation='all' should be rendered with replacement");
        Assertions.assertTrue(template.contains("validation=\"value123\""), "Medusa tag m:validation='value123' should be rendered with replacement");
    }

    @Test
    void combineClasses() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(withClassesHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("test321 red"), "Medusa tag m:validation='all' should keep existing classes");
        Assertions.assertTrue(template.contains("test123 orange"), "Medusa tag m:validation='value123' should be keep existing classes");
        Assertions.assertTrue(template.contains("error"), "Medusa tag m:validation should add an error class to the ul");
        Assertions.assertTrue(template.contains("error hidden"), "Medusa tag m:validation should add an error and a hidden class to the validation p element");
    }
}
