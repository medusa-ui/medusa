package io.getmedusa.medusa.core.router;

import io.getmedusa.medusa.core.cache.HTMLCache;
import io.getmedusa.medusa.core.injector.HTMLInjector;
import io.getmedusa.medusa.core.registry.PageTitleRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.util.FilenameHandler;
import io.getmedusa.medusa.core.websocket.ReactiveWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.HTMLInjector.CHARSET;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * The HTML router is a bean that handles the setup of routes with Spring WebFlux. Before this class is initialized, the beforeInitialization post-processor has ran,
 * which in turn has filled up all registries. As such, we can use the {@link RouteRegistry} here to setup up all routes.
 */
@Component
class HTMLRouter {

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/websocket.js") Resource scripts,
                                                     @Value("classpath:/medusa-default-styling.css") Resource style,
                                                     IRequestStreamHandler requestStreamHandler) {
        final String script = loadScript(scripts);
        final String styling = loadDefaultStyle(style);
        return RouteRegistry.getInstance().getRoutesWithHTMLFile().stream().map(route -> {
            String fileName = getPath(loadHTMLIntoCache(route.getValue(), Integer.toString(route.getKey().hashCode())));
            return route(
                    GET(route.getKey()),
                    requestStreamHandler.handle(script, styling, fileName));
        })
        .reduce(RouterFunction::and)
        .orElse(null);
    }

    private String loadDefaultStyle(Resource style) {
        return "<style>" + loadScript(style) + "</style>";
    }

    private String loadScript(Resource scripts) {
        try {
            return StreamUtils.copyToString(scripts.getInputStream(), CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private Resource loadHTMLIntoCache(String htmlFile, String hash) {
        String resourcePath = FilenameHandler.normalize(htmlFile);
        Resource html = resourceLoader.getResource("classpath:/" + resourcePath);
        try {
            final String fileName = getPath(html);
            if(!PageTitleRegistry.getInstance().hasTitle(hash)) {
                String htmlContent = HTMLCache.getInstance().getHTMLOrAdd(fileName, StreamUtils.copyToString(html.getInputStream(), CHARSET));
                PageTitleRegistry.getInstance().addTitle(hash, htmlContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    private String getPath(Resource html) {
        return FilenameHandler.removeExtension(((ClassPathResource) html).getPath());
    }

    @Bean
    public RouterFunction<ServerResponse> resourcesRouter() {
        return RouterFunctions
                .resources("/static/**", new ClassPathResource("static/"));
    }

    @Bean
    public HandlerMapping handlerMapping(ReactiveWebSocketHandler handler) {
        Map<String, ReactiveWebSocketHandler> map = new HashMap<>();
        RouteRegistry.getInstance().getRoutesWithHTMLFile().forEach(route ->
                map.put(HTMLInjector.EVENT_EMITTER + route.getKey().hashCode(), handler));
        mapping.setUrlMap(map);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return mapping;
    }
}
