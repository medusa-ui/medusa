package io.getmedusa.medusa.core.boot.hydra;

import io.getmedusa.medusa.core.boot.Fragment;
import io.getmedusa.medusa.core.boot.RouteDetection;
import io.getmedusa.medusa.core.boot.hydra.config.MedusaConfigurationProperties;
import io.getmedusa.medusa.core.boot.hydra.model.FragmentHydraRequestWrapper;
import io.getmedusa.medusa.core.boot.hydra.model.meta.ActiveService;
import io.getmedusa.medusa.core.boot.hydra.model.meta.RenderedFragment;
import io.getmedusa.medusa.core.router.request.Route;
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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(name = "medusa.hydra.uri")
@Component
public class HydraConnectionController {

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
                                     MedusaConfigurationProperties configProps,
                                     @Value("${spring.rsocket.server.port}") Integer socketPort,
                                     @Value("${server.port}") Integer serverPort) {
        this.privateKey = configProps.getHydra().getSecret().getPrivateKey();

        this.registrationURL = webClient.post().uri(configProps.getHydra().registrationURL());
        this.isAliveURL = webClient.post().uri(configProps.getHydra().isAliveURL());
        this.requestFragmentURL = webClient.post().uri(configProps.getHydra().requestFragmentURL());

        activeService = new ActiveService();
        activeService.setName(configProps.getName());
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

    //@PostConstruct
    public void enableHydraConnectivity() {
        this
                .getActiveService()
                .getEndpoints().addAll(
                        RouteDetection.INSTANCE.getDetectedRoutes()
                                .stream()
                                .map(Route::getPath)
                                .toList());
        this.state = ConnectivityState.NOT_REGISTERED;
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
                return response.bodyToMono(List.class);
            } else {
                registrationFailure(null);
                return Mono.empty();
            }
        })
        .doOnError(this::registrationFailure)
        .onErrorReturn(new ArrayList())
        .subscribe(l -> {
            System.out.println(l);
        });
    }

    private void registrationFailure(Throwable e) {
        if (!hasShownConnectionError) {
            logger.error("Connection to Hydra failed, retrying every second", e);
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

    public Mono<List<RenderedFragment>> askHydraForFragment(Map<String, List<Fragment>> requests, Map<String, Object> attributes) {
        FragmentHydraRequestWrapper wrapper = new FragmentHydraRequestWrapper();
        wrapper.setAttributes(attributes);
        wrapper.setRequests(requests);
        return requestFragmentURL
                .bodyValue(wrapper)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(RenderedFragment[].class);
                    } else {
                        return Mono.just(new RenderedFragment[]{});
                    }
                })
                .map(x -> Arrays.stream(x).toList())
                .doOnError(err -> {})
                .onErrorReturn(List.of());
    }

    private enum ConnectivityState {
        INITIALIZING,
        NOT_REGISTERED,
        REGISTERED
    }

}
