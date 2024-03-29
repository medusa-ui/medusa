package io.getmedusa.medusa.core.config;

import io.getmedusa.medusa.core.memory.SessionUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
/*@EnableReactiveMethodSecurity*/
public class RSocketSecurityConfiguration {

    @Bean
    RSocketMessageHandler messageHandler(RSocketStrategies strategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        handler.setRSocketStrategies(strategies);
        return handler;
    }

    @Bean
    PayloadSocketAcceptorInterceptor authorization(RSocketSecurity security) {
        security.authorizePayload(authorize -> authorize.anyExchange().permitAll())
                .simpleAuthentication(s -> s.authenticationManager(new UserDetailsRepositoryReactiveAuthenticationManager(new SessionUserRepository())));
        return security.build();
    }
}