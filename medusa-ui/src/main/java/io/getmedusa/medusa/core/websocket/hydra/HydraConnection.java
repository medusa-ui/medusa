package io.getmedusa.medusa.core.websocket.hydra;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.registry.HydraRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.util.ObjectMapperBuilder;
import io.getmedusa.medusa.core.websocket.hydra.meta.HydraStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value="hydra.enabled", havingValue = "true")
public class HydraConnection implements ApplicationListener<ContextRefreshedEvent> {

    private static final String HYDRA_HEALTH_WS_URI = "ws://localhost:8761/services/health";

    private final ObjectMapper objectMapper = ObjectMapperBuilder.setupObjectMapper();
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
            hydraHealthRegistration.setMenuItems(RouteRegistry.getInstance().getMenuItems());
            hydraHealthRegistration.setStaticResources(determineExtensionsOfStaticResources());
            healthRegistrationJSON = objectMapper.writeValueAsString(hydraHealthRegistration);

            connectToHydra();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private WebSocketSession activeSession = null;

    private void connectToHydra() {
        if(activeSession == null) {
            System.out.println("Connecting to hydra ...");
            new ReactorNettyWebSocketClient()
                    .execute(URI.create(HYDRA_HEALTH_WS_URI), session -> {
                        activeSession = session;
                        return session
                                .send(Flux.just(session.textMessage(healthRegistrationJSON)))
                                .and(session.receive().map(x -> {
                                    reactToIncomingUpdate(x.getPayloadAsText());
                                    return Mono.empty();
                                }))
                                .doFinally(x -> {
                                    System.err.println(x);
                                    session.close().subscribe();
                                    activeSession = null;
                                    connectToHydra();
                                } ); })
                    .retryWhen(Retry.indefinitely())
                    .subscribe();
        }
    }

    private void reactToIncomingUpdate(String payloadAsText) {
        new Thread(() -> {
            try {
                HydraRegistry.update(objectMapper.readValue(payloadAsText, HydraStatus.class));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }).start();
    }

    private Set<String> determineExtensionsOfStaticResources() throws IOException {
        Resource[] resources = resourceResolver.getResources("classpath:*/**.*");
        return Arrays.stream(resources)
                .map(Resource::getFilename)
                .filter(Objects::nonNull)
                .map(filename -> filename.substring(filename.lastIndexOf('.')+1))
                .collect(Collectors.toSet());
    }
}
