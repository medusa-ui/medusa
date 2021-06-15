package io.getmedusa.medusa;

import io.getmedusa.medusa.core.injector.HTMLInjector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

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
		return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(HTMLInjector.INSTANCE.inject(html)));
	}

}
