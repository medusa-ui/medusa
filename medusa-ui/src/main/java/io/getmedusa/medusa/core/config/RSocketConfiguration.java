package io.getmedusa.medusa.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
public class RSocketConfiguration {

    @Value("${spring.rsocket.server.port}")
    private int port;

    @Value("${spring.rsocket.server.mapping-path}")
    private String mappingPath;

    @Bean
    public Mono<RSocketRequester> rSocketRequester(RSocketStrategies rSocketStrategies) {
        return Mono.just(RSocketRequester.builder()
                .rsocketStrategies(rSocketStrategies)
                .websocket(getURI()));
    }

    private URI getURI() {
        return URI.create(String.format("ws://localhost:%d%s", port, mappingPath));
    }

}
