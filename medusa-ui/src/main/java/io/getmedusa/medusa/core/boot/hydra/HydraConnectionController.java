package io.getmedusa.medusa.core.boot.hydra;

import io.getmedusa.medusa.core.boot.hydra.model.meta.ActiveService;
import io.getmedusa.medusa.core.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import reactor.core.publisher.Mono;
import io.getmedusa.medusa.tags.action.HydraConnection;

import java.net.InetAddress;

@ConditionalOnProperty(name = "medusa.hydra.uri")
@Component
public class HydraConnectionController implements HydraConnection {

    private final String privateKey;

    private final ActiveService activeService;

    private final RequestBodySpec registrationURL;
    private final RequestBodySpec isAliveURL;
    private final RequestBodySpec requestFragmentURL;

    private ConnectivityState state = ConnectivityState.INITIALIZING;
    private boolean hasShownConnectionError = false;
    private long downtimeStart;

    private static final Logger logger = LoggerFactory.getLogger(HydraConnectionController.class);

    public HydraConnectionController(WebClient webClient,
                                     @Value("${medusa.hydra.secret.public}") String publicKey,
                                     @Value("${medusa.hydra.secret.private}") String privateKey,
                                     @Value("${medusa.hydra.uri}") String uri,
                                     @Value("${spring.rsocket.server.port}") Integer socketPort,
                                     @Value("${server.port}") Integer serverPort,
                                     @Value("${medusa.name}") String appName) {
        this.privateKey = privateKey;

        this.registrationURL = webClient.post().uri(uri + "/h/discovery/_publicKey_/registration".replace("_publicKey_", publicKey));
        this.isAliveURL = webClient.post().uri(uri + "/h/discovery/_publicKey_/alive".replace("_publicKey_", publicKey));
        this.requestFragmentURL = webClient.post().uri(uri + "/h/discovery/_publicKey_/requestFragment".replace("_publicKey_", publicKey));

        activeService = new ActiveService();
        activeService.setName(appName);
        activeService.setHost(getCurrentIP());
        activeService.setPort(serverPort);
        activeService.setWebProtocol("http");
        //finish setup in dynamic detection
    }

    public ActiveService getActiveService() {
        return activeService;
    }

    @Scheduled(fixedDelay = 1000)
    public void retryRegistration() {
        if(ConnectivityState.NOT_REGISTERED.equals(state)) {
            sendRegistration();
        }
    }

    public void sendRegistration() {
        registrationURL.bodyValue(activeService).exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                state = ConnectivityState.REGISTERED;
                if(hasShownConnectionError) {
                    logger.info("Connection to Hydra restored, downtime of {}", TimeUtils.diffString(downtimeStart, TimeUtils.now()));
                    hasShownConnectionError = false;
                    downtimeStart = 0L;
                }
                return response.bodyToMono(String.class);
            } else {
                registrationFailure(null);
                return Mono.empty();
            }
        })
        .doOnError(this::registrationFailure)
        .onErrorReturn("")
        .subscribe();
    }

    private void registrationFailure(Throwable e) {
        if (!hasShownConnectionError) {
            logger.error("Connection to Hydra failed, retrying every second");
            if(e != null) { aliveFailure(e); }
            hasShownConnectionError = true;
        }
    }

    @Scheduled(fixedDelay = 3000)
    public void scheduleIsAlive() {
        if(ConnectivityState.REGISTERED.equals(state)) {
            sendIsAlive();
        }
    }

    public void sendIsAlive() {
        isAliveURL.bodyValue(activeService.getName()).exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(String.class);
            } else {
                aliveFailure(null);
                return Mono.empty();
            }
        })
                .doOnError(this::aliveFailure)
                .onErrorReturn("Failed registration")
                .subscribe();
    }

    private void aliveFailure(Throwable e) {
        this.state = ConnectivityState.NOT_REGISTERED;
        if(this.downtimeStart == 0L) {
            this.downtimeStart = TimeUtils.now();
        }
    }

    private String getCurrentIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<String> askHydraForFragment(String service, String ref) {
        return requestFragmentURL
                .bodyValue(activeService.getName())
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(String.class);
                    } else {
                        aliveFailure(null);
                        return Mono.empty();
                    }
                })
                .doOnError(this::aliveFailure)
                .onErrorReturn("Failed registration");
    }

    private enum ConnectivityState {
        INITIALIZING,
        NOT_REGISTERED,
        REGISTERED
    }

}
