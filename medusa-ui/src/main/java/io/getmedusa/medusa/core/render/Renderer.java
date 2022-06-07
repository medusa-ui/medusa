package io.getmedusa.medusa.core.render;

import io.getmedusa.medusa.core.session.Session;
import org.springframework.stereotype.Component;

@Component
public class Renderer {

    public String render(String templateHTML, Session session) {

        session.setLastRenderedHTML(templateHTML);
        return templateHTML;
    }
}
