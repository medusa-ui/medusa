package io.getmedusa.medusa.core.boot.hydra.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(MedusaConfigurationProperties medusaConfigurationProperties) {
        int webclientTimeout = medusaConfigurationProperties.getHydra().getWebclientTimeout();
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webclientTimeout)
                .responseTimeout(Duration.ofMillis(webclientTimeout))
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(webclientTimeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(webclientTimeout, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
