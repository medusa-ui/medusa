package io.getmedusa.medusa.core.router.request;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.getmedusa.medusa.core.boot.RouteDetection.INSTANCE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * The initial request only resolves if it matches a route set up initially in the RouteSetup class.
 * This is a class that connects the routes loaded during at-startup initialization with a process to handle its rendering.
 */
@Component
public class RouteSetup {

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(IRequestStreamHandler requestStreamHandler) {
        if(null == requestStreamHandler) { throw throw404(); }

        return INSTANCE.getDetectedRoutes().stream()
                .map(route -> route(
                        GET(route.getPath()),
                        requestStreamHandler.startSessionAndBuildHTML(route)))
                .reduce(RouterFunction::and)
                .orElseThrow(this::throw404);
    }

    private IllegalStateException throw404() {
        return new IllegalStateException("Could not route");
    }

    @Bean
    public RouterFunction<ServerResponse> resourcesRouter() {
        return resources("/static/**", new ClassPathResource("static/"));
    }

}
