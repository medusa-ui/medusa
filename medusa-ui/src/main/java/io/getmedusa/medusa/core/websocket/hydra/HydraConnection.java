package io.getmedusa.medusa.core.websocket.hydra;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getmedusa.medusa.core.config.HydraConfig;
import io.getmedusa.medusa.core.filters.JWTTokenInterpreter;
import io.getmedusa.medusa.core.injector.HydraURLReplacer;
import io.getmedusa.medusa.core.registry.HydraRegistry;
import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.util.ObjectMapperBuilder;
import io.getmedusa.medusa.core.websocket.hydra.meta.HydraStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
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
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value="hydra.enabled", havingValue = "true")
public class HydraConnection implements ApplicationListener<ApplicationEvent> {

    private final String hydraHealthWsUri;

    private final ObjectMapper objectMapper = ObjectMapperBuilder.setupObjectMapper();
    private String healthRegistrationJSON = null;

    private final HydraHealthRegistration hydraHealthRegistration;
    final ResourcePatternResolver resourceResolver;

    public static RSAPublicKey publicKey = null;

    public HydraConnection(HydraConfig hydraConfig,
                           ResourcePatternResolver resourceResolver,
                           BuildProperties buildProperties) {
        if(hydraConfig.getSecret().length() < 32) throw new SecurityException("Hydra secret must at least be 32 characters long");
        this.hydraHealthRegistration = new HydraHealthRegistration(buildProperties.getName(),
                                                                    hydraConfig.getSecret(),
                                                                    buildVersion(buildProperties),
                                                                    hydraConfig.getAwakeningType());
        this.resourceResolver = resourceResolver;
        this.hydraHealthWsUri = "ws://URI/services/health".replace("URI", hydraConfig.getUrl());
    }

    private long buildVersion(BuildProperties buildProperties) {
        return buildProperties.getTime().getEpochSecond();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        try {
            if(event instanceof ContextRefreshedEvent ) {
                HydraURLReplacer.STATIC_RESOURCES = determineListOfStaticResources();

                hydraHealthRegistration.setEndpoints(RouteRegistry.getInstance().getRoutes());
                hydraHealthRegistration.setWebsockets(RouteRegistry.getInstance().getWebSockets());
                hydraHealthRegistration.setMenuItems(RouteRegistry.getInstance().getMenuItems());
                hydraHealthRegistration.setStaticResources(determineExtensionsOfStaticResources());
                healthRegistrationJSON = objectMapper.writeValueAsString(hydraHealthRegistration);

                connectToHydra();
            } else if(event instanceof WebServerInitializedEvent e) {
                hydraHealthRegistration.setPort(e.getWebServer().getPort());
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private WebSocketSession activeSession = null;

    private void connectToHydra() {
        if(activeSession == null) {
            System.out.println("Connecting to hydra ...");
            new ReactorNettyWebSocketClient()
                    .execute(URI.create(hydraHealthWsUri), session -> {
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
                if(payloadAsText.contains("pub-key")) {
                    handleUpdateToPublicKey(objectMapper, payloadAsText);
                } else {
                    HydraRegistry.update(objectMapper.readValue(payloadAsText, HydraStatus.class));
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }).start();
    }

    private static void handleUpdateToPublicKey(ObjectMapper mapper, String payloadAsText) throws Exception {
        Map<String, String> pubKey = mapper.readValue(payloadAsText, Map.class);
        byte[] decodedKey = Base64.getDecoder().decode(pubKey.get("pub-key"));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        publicKey = (RSAPublicKey) kf.generatePublic(spec);
        JWTTokenInterpreter.clearCache();
    }

    private Set<String> determineListOfStaticResources() throws IOException {
        Resource[] resources = resourceResolver.getResources("classpath:**/**.*");
        Set<String> set = new HashSet<>();
        for (Resource r : resources) {
            String path = r.getURI().getPath();
            if (path != null && path.contains("/static/")) {
                set.add(path.substring(path.indexOf("/static/") + 8));
            }
        }
        return set;
    }

    private Set<String> determineExtensionsOfStaticResources() {
        return HydraURLReplacer.STATIC_RESOURCES.stream()
                .map(filename -> filename.substring(filename.lastIndexOf('.')+1))
                .collect(Collectors.toSet());
    }
}
