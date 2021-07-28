package io.getmedusa.medusa.core.router;

import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static io.getmedusa.medusa.core.injector.HTMLInjector.INSTANCE;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@ConditionalOnClass(ReactiveSecurityContextHolder.class)
public class RequestStreamHandlerWithSecurity implements IRequestStreamHandler {

    @Override
    public HandlerFunction<ServerResponse> handle(String script, String styling, String fileName) {
        return request -> ReactiveSecurityContextHolder.getContext().flatMap(authenticationHolder ->
                {
                    try {
                        final SecurityContext securityContext = new SecurityContext(authenticationHolder.getAuthentication());
                        return ok().contentType(MediaType.TEXT_HTML).bodyValue(INSTANCE.inject(request, securityContext, fileName, script, styling));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Mono.error(e);
                    }
                }
        );
    }

}
