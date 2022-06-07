package io.getmedusa.medusa.core.render;

import io.getmedusa.medusa.core.session.Session;
import org.springframework.stereotype.Component;

/**
 * Thymeleaf renderer
 */
@Component
public class Renderer {

    public String render(String templateHTML, Session session) {

        return templateHTML;
    }
}
