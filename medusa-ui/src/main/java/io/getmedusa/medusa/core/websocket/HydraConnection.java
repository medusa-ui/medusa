package io.getmedusa.medusa.core.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value="hydra.enabled", havingValue = "true")
public class HydraConnection implements DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private static final String HYDRA_HEALTH_URI = "http://localhost:8761/services/register";
    private static final String HYDRA_KILL_URI = "http://localhost:8761/services/kill";

    private static final String HYDRA_HEALTH_WS_URI = "ws://localhost:8761/services/health";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient client = setupWebClient();
    private String healthRegistrationJSON = null;

    private final HydraHealthRegistration hydraHealthRegistration;
    final ResourcePatternResolver resourceResolver;

    public HydraConnection(@Value("${server.port:8080}") int port, @Value("${hydra.name}") String name, ResourcePatternResolver resourceResolver) {
        this.hydraHealthRegistration = new HydraHealthRegistration(port, name);
        this.resourceResolver = resourceResolver;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            hydraHealthRegistration.setEndpoints(RouteRegistry.getInstance().getRoutes());
            hydraHealthRegistration.setWebsockets(RouteRegistry.getInstance().getWebSockets());
            hydraHealthRegistration.setStaticResources(determineExtensionsOfStaticResources());
            healthRegistrationJSON = objectMapper.writeValueAsString(hydraHealthRegistration);

            new ReactorNettyWebSocketClient()
                    .execute(URI.create(HYDRA_HEALTH_WS_URI), session -> session
                            .send(Flux.just(session.textMessage(healthRegistrationJSON))).and(session.receive()))
                    .retryWhen(Retry.indefinitely())
                    .subscribe();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Set<String> determineExtensionsOfStaticResources() throws IOException {
        Resource[] resources = resourceResolver.getResources("classpath:*/**.*");
        return Arrays.stream(resources)
                .map(Resource::getFilename)
                .filter(Objects::nonNull)
                .map(filename -> filename.substring(filename.lastIndexOf('.')+1))
                .collect(Collectors.toSet());
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
