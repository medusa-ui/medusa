package io.getmedusa.medusa.core.router;

import io.getmedusa.medusa.core.registry.ActiveSessionRegistry;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.getmedusa.medusa.core.injector.HTMLInjector.INSTANCE;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * Standard implementation of the {@link IRequestStreamHandler}. This class will be used only when there is no Spring Security present in the client.
 * As such, the passed along security context is always null.
 */
@Component
@ConditionalOnMissingBean(RequestStreamHandlerWithSecurity.class)
class RequestStreamHandler implements IRequestStreamHandler {

    @Override
    public HandlerFunction<ServerResponse> handle(String script, String styling, String fileName) {
        final SecurityContext securityContext = new SecurityContext(null);
        ActiveSessionRegistry.getInstance().registerSecurityContext(securityContext);
        return request ->ok().contentType(MediaType.TEXT_HTML).bodyValue(INSTANCE.inject(request, securityContext, fileName, script, styling));
    }

    @Override
    public boolean hasSecurity() {
        return false;
    }

}
