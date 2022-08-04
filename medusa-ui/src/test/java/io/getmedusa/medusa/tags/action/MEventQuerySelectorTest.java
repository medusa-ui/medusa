package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.util.FluxUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MEventQuerySelectorTest extends MedusaTagTest {

    private final String thisTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input m:change="action(:{this})"></div>
            </body>
            </html>
            """;

    private final String thisValueTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input m:change="action(:{this.value})"></div>
            </body>
            </html>
            """;

    private final String thisAttributeTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input type="text" m:change="action(:{this.type})"></div>
            </body>
            </html>
            """;

    private final String elementIDTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input id="my-id"></div>
                <div><button m:click="action(:{#my-id})">click me</button></div>
            </body>
            </html>
            """;

    private final String elementIDValueTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input id="my-id"></div>
                <div><button m:click="action(:{#my-id.value})">click me</button></div>
            </body>
            </html>
            """;

    private final String elementIDAttributeTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input type="text" id="my-id"></div>
                <div><button m:click="action(:{#my-id}, :{#my-id.type})">click me</button></div>
            </body>
            </html>
            """;

    private final String classTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input class="my-class"></div>
                <div><button m:click="action(:{.my-class})">click me</button></div>
            </body>
            </html>
            """;

    private final String classValueTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input class="my-class"></div>
                <div><button m:click="action(:{.my-class.value})">click me</button></div>
            </body>
            </html>
            """;
    private final String classAttributeTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input type="text" class="my-class"></div>
                <div><button m:click="action(:{.my-class},:{.my-class.type})">click me</button></div>
            </body>
            </html>
            """;
    private final String multiClassTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input type="text" class="my-class my-sub-class"></div>
                <div><button m:click="action(:{.my-class.my-sub-class})">click me</button></div>
            </body>
            </html>
            """;
    private final String multiClassAttributeTemplateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <div><input type="text" class="my-class my-sub-class"></div>
                <div><button m:click="action(:{.my-class.my-sub-class.type})">click me</button></div>
            </body>
            </html>
            """;

    @Test
    void thisTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(thisTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onchange=\"_M.doAction(null, `action('${this.value}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void thisValueTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(thisValueTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onchange=\"_M.doAction(null, `action('${this.value}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void thisAttributeTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(thisAttributeTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onchange=\"_M.doAction(null, `action('${this.type}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void elementIDTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(elementIDTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('${document.querySelector('#my-id').value}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void elementIDValueTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(elementIDValueTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('${document.querySelector('#my-id').value}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void elementIDAttributeTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(elementIDAttributeTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('${document.querySelector('#my-id').value}', '${document.querySelector('#my-id').type}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void classTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(classTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('${document.querySelector('.my-class').value}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void classValueTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(classValueTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('${document.querySelector('.my-class').value}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void classAttributeTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(classAttributeTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('${document.querySelector('.my-class').value}','${document.querySelector('.my-class').type}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void multiclassAttributeTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(multiClassTemplateHTML, session));
        logger.info(template);
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('${document.querySelector('.my-class.my-sub-class').value}')`)\""), "Medusa tag should be able to find element values");
    }

    @Test
    void multiclassTemplateHTMLTest() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(multiClassAttributeTemplateHTML, session));
        logger.debug(template);
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, `action('${document.querySelector('.my-class.my-sub-class').type}')`)\""), "Medusa tag should be able to find element values");
    }
}
