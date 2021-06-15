package io.getmedusa.medusa;

import io.getmedusa.medusa.core.websocket.Event;
import io.getmedusa.medusa.core.websocket.ReactiveWebSocketHandler;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.util.HashMap;
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

	@Bean
	public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/hello-world.html") Resource html) {
		return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(INSTANCE.inject(html)));
	}

	@Bean
	public HandlerMapping webSocketHandlerMapping(UnicastProcessor<Event> messagePublisher, Flux<Event> messages) {
		Map<String, WebSocketHandler> map = new HashMap<>();
		map.put("/event-emitter", new ReactiveWebSocketHandler(messagePublisher, messages));

		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setOrder(1);
		handlerMapping.setUrlMap(map);
		return handlerMapping;
	}

	@Bean
	public UnicastProcessor<Event> messagePublisher(){
		return UnicastProcessor.create();
	}

	@Bean
	public Flux<Event> messages(UnicastProcessor<Event> messagePublisher) {
		return messagePublisher
				.replay(25)
				.autoConnect();
	}

	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

}
