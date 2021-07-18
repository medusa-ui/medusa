package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Service
@ConditionalOnProperty(value="hydra.enabled", havingValue = "true")
public class HydraConnection implements DisposableBean {

    private static final String HYDRA_HEALTH_URI = "http://localhost:8761/services/register";
    private static final String HYDRA_KILL_URI = "http://localhost:8761/services/kill";

    private static final String HYDRA_HEALTH_WS_URI = "ws://localhost:8761/services/health";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient client = setupWebClient();
    private String healthRegistrationJSON = null;

    private final HydraHealthRegistration hydraHealthRegistration;
    public HydraConnection(@Value("${server.port:8080}") int port, @Value("${hydra.name}") String name) {
        this.hydraHealthRegistration = new HydraHealthRegistration(port, name);
    }

    @PostConstruct
    public void init() throws JsonProcessingException {
        hydraHealthRegistration.setEndpoints(RouteRegistry.getInstance().getRoutes());
        hydraHealthRegistration.setWebsockets(RouteRegistry.getInstance().getWebSockets());
        healthRegistrationJSON = objectMapper.writeValueAsString(hydraHealthRegistration);

        new ReactorNettyWebSocketClient()
                .execute(URI.create(HYDRA_HEALTH_WS_URI), session -> session
                        .send(Flux.just(session.textMessage(healthRegistrationJSON))).and(session.receive()))
                .retryWhen(Retry.indefinitely())
                .subscribe();

        //healthPing();
    }

    private void healthPing() {
        client
            .post()
            .uri(HYDRA_HEALTH_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(healthRegistrationJSON)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(Retry.indefinitely())
            .delaySubscription(Duration.ofSeconds(5))
            .repeat()
            .subscribe();
    }

    @Override
    public void destroy() {
        killPing();
    }

    private void killPing() {
        client
            .post()
            .uri(HYDRA_KILL_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(healthRegistrationJSON)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class)
            .subscribe();
    }

    private WebClient setupWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .responseTimeout(Duration.ofSeconds(1))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(1, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(1, TimeUnit.SECONDS)));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }


}
