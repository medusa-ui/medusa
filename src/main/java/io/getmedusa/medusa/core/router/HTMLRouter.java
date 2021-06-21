package io.getmedusa.medusa.core.router;

import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.injector.HTMLInjector;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.websocket.ReactiveWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.HashMap;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.HTMLInjector.INSTANCE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class HTMLRouter {

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/websocket.js") Resource scripts) {
        return RouteRegistry.getInstance().getAllPageSetups().stream().map(pageSetup -> {
            String resourcePath = pageSetup.getHtmlFile();
            if(!resourcePath.endsWith(".html")) resourcePath = resourcePath + ".html";
            Resource html = resourceLoader.getResource("classpath:/" + resourcePath);
            return route(
                    GET(pageSetup.getGetPath()),
                    request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(INSTANCE.inject(html, scripts)));
        })
        .reduce(RouterFunction::and)
        .orElse(null);
    }

    @Bean
    public RouterFunction<ServerResponse> resourcesRouter() {
        return RouterFunctions
                .resources("/static/**", new ClassPathResource("static/"));
    }

    @Bean
    public HandlerMapping handlerMapping(ReactiveWebSocketHandler handler) {
        Map<String, ReactiveWebSocketHandler> map = new HashMap<>();
        RouteRegistry.getInstance().getAllPageSetups()
                .stream()
                .map(PageSetup::getHtmlFile)
                .forEach(file -> map.put(HTMLInjector.EVENT_EMITTER + file, handler));
        mapping.setUrlMap(map);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return mapping;
    }
}
