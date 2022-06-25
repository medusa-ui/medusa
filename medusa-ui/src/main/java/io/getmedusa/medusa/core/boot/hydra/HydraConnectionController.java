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
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import reactor.core.publisher.Mono;

import java.net.InetAddress;

@ConditionalOnProperty(name = "medusa.hydra.uri")
@Component
public class HydraConnectionController {

    private final WebClient client;
    private final String publicKey;
    private final String privateKey;

    private final String hydraBaseURI;

    private final ActiveService activeService;

    private ConnectivityState state = ConnectivityState.NOT_REGISTERED;
    private boolean hasShownConnectionError = false;
    private long downtimeStart;

    private static final Logger logger = LoggerFactory.getLogger(HydraConnectionController.class);

    public HydraConnectionController(WebClient webClient,
                                     @Value("${medusa.hydra.secret.public}") String publicKey,
                                     @Value("${medusa.hydra.secret.private}") String privateKey,
                                     @Value("${medusa.hydra.uri}") String uri,
                                     @Value("${spring.rsocket.server.port}") Integer port,
                                     @Value("${medusa.name}") String appName) {
        this.client = webClient;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.hydraBaseURI = uri;

        activeService = new ActiveService();
        activeService.setName(appName);
        activeService.setHost(getCurrentIP());
        activeService.setPort(port);
        sendRegistration();
    }

    @Scheduled(fixedDelay = 1000)
    public void retryRegistration() {
        if(ConnectivityState.NOT_REGISTERED.equals(state)) {
            sendRegistration();
        }
    }

    public void sendRegistration() {
        UriSpec<RequestBodySpec> uriSpec = client.post();
        String uri = hydraBaseURI + "/h/discovery/_publicKey_/registration".replace("_publicKey_", publicKey);
        RequestBodySpec bodySpec = uriSpec.uri(uri);
        RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(activeService);

        headersSpec.exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                state = ConnectivityState.REGISTERED;
                if(hasShownConnectionError) {
                    hasShownConnectionError = false;
                    logger.info("Connection to Hydra restored, downtime of {}s", TimeUtils.secondsDiff(downtimeStart, TimeUtils.now()));
                    downtimeStart = 0L;
                }
                return response.bodyToMono(String.class);
            } else {
                registrationFailure();
                return Mono.empty();
            }
        })
        .doOnError(throwable -> registrationFailure())
        .onErrorReturn("Failed registration")
        .subscribe();
    }

    private void registrationFailure() {
        aliveFailure();
        if (!hasShownConnectionError) {
            logger.error("Connection to Hydra failed, retrying every second");
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
        UriSpec<RequestBodySpec> uriSpec = client.post();
        String uri = hydraBaseURI + "/h/discovery/_publicKey_/alive".replace("_publicKey_", publicKey);
        RequestBodySpec bodySpec = uriSpec.uri(uri);
        RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(activeService.getName());

        headersSpec.exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(String.class);
            } else {
                aliveFailure();
                return Mono.empty();
            }
        }).doOnError(throwable -> aliveFailure())
                .onErrorReturn("Failed registration")
                .subscribe();
    }

    private void aliveFailure() {
        state = ConnectivityState.NOT_REGISTERED;
        if(downtimeStart == 0L) {
            downtimeStart = TimeUtils.now();
        }
    }

    private String getCurrentIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private enum ConnectivityState {
        NOT_REGISTERED,
        REGISTERED
    }

}
