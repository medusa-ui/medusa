package io.getmedusa.medusa.core.render;

import io.getmedusa.medusa.core.session.Session;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import reactor.core.publisher.Flux;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Thymeleaf renderer
 */
@Component
public class Renderer {

    private final SpringWebFluxTemplateEngine engine;
    private final DataBufferFactory bufferFactory;
    final IEngineConfiguration configuration;
    public Renderer() {
        this.bufferFactory = new DefaultDataBufferFactory();

        SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setEnableSpringELCompiler(true);
        this.engine = templateEngine;
        this.configuration = engine.getConfiguration();
    }

    public Flux<DataBuffer> render(String templateHTML, Session session) {
        Set<String> markupSelectors = new HashSet<>(); //see https://www.baeldung.com/spring-thymeleaf-fragments#markup-selector

        IContext context = new EngineContext(configuration, null, new HashMap<>(), Locale.getDefault(), session.toLastParameterMap());
        return Flux.from(engine.processStream(appendRSocketScript(templateHTML), markupSelectors, context, bufferFactory, MediaType.TEXT_HTML, UTF_8));
    }

    public String appendRSocketScript(String rawTemplate) {
        return rawTemplate.replace("</body>", "\t<script src=\"websocket.js\"></script>\n</body>");
    }

}
