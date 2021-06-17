package io.getmedusa.medusa;

import io.getmedusa.medusa.core.injector.HTMLInjector;
import io.getmedusa.medusa.core.websocket.ReactiveWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.HTMLInjector.INSTANCE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class MedusaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedusaApplication.class, args);
	}

	private Map<RequestPredicate, String> urlMapping = setupUrlMapping();
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	private Map<RequestPredicate, String> setupUrlMapping() {
		Map<RequestPredicate, String> urlMapping = new HashMap<>();
		urlMapping.put(GET("/"), "hello-world.html");
		return urlMapping;
	}

	@Bean
	public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/websocket.js") Resource scripts) {
		List<RouterFunction<ServerResponse>> routerFunctions = new ArrayList<>();
		urlMapping.forEach((key, value) -> {
			Resource html = resourceLoader.getResource("classpath:/" + value);
			routerFunctions.add(route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(INSTANCE.inject(html, scripts))));
		});
		return routerFunctions.stream()
				.reduce(RouterFunction::and)
				.orElse(null);
	}

	@Bean
	public HandlerMapping handlerMapping(ReactiveWebSocketHandler handler) {
		Map<String, WebSocketHandler> map = new HashMap<>();
		urlMapping.forEach((key, value) -> map.put(HTMLInjector.EVENT_EMITTER + value, handler));
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setUrlMap(map);
		mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return mapping;
	}

}
