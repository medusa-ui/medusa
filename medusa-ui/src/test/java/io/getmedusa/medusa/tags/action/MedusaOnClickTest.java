package io.getmedusa.medusa.tags.action;

import io.getmedusa.medusa.core.render.Renderer;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.FluxUtils;
import io.getmedusa.medusa.tags.MedusaDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class MedusaOnClickTest {

    private Renderer renderer;
    private final Session session = buildSession();

    private Session buildSession() {
        final Session s = new Session();
        s.setLastUsedHash("");
        return s;
    }

    @BeforeEach
    public void init() {
        this.renderer = new Renderer(Set.of(new MedusaDialect(Set.of(new MedusaOnClick()))), null);
    }

    private final String templateHTML = """
            <!DOCTYPE html>
            <html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:m="https://www.getmedusa.io/medusa.xsd">
            <body>
                <p>Value: <span th:text="${counter}"></span></p>
                <button m:click="increment()">increment</button>
            </body>
            </html>
            """;

    @Test
    void testRender() {
        String template = FluxUtils.dataBufferFluxToString(renderer.render(templateHTML, session));
        System.out.println(template);
        Assertions.assertFalse(template.contains("th:text="), "Thymeleaf tags should be rendered");
        Assertions.assertFalse(template.contains("m:click="), "Medusa tags should be rendered");
        Assertions.assertTrue(template.contains("onclick=\"_M.doAction(null, 'increment()')\""), "Medusa tag should be rendered with replacement JS");
    }

}
