package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(value="hydra.enabled", havingValue = "true")
public class HydraConnection {

    private ObjectMapper objectMapper = new ObjectMapper();

    HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .responseTimeout(Duration.ofMillis(5000))
            .doOnConnected(conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

    WebClient client = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();

    private HydraHealthRegistration hydraHealthRegistration;
    public HydraConnection(@Value("${server.port:8080}") int port, @Value("${hydra.name}") String name) {
        this.hydraHealthRegistration = new HydraHealthRegistration(port, name);
    }

    @PostConstruct
    public void init() {
        hydraHealthRegistration.setEndpoints(RouteRegistry.getInstance().getRoutes());
        Flux
                .interval(Duration.ofSeconds(30))
                .subscribe(x -> {
                    try {
                        client
                                .post()
                                .uri("http://localhost:8761/services/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(objectMapper.writeValueAsString(hydraHealthRegistration))
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(String.class).onErrorResume(y -> Mono.empty()).subscribe(System.out::println);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
    }

}
