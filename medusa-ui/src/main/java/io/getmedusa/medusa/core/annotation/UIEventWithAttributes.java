package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * Interface enforcing the standardized way of calling for initial variables
 */
public interface UIEventWithAttributes {

    /**
     * Standardized way of calling setupAttributes()
     *
     * @param request incoming {@link ServerRequest}, optional in use internally
     * @param securityContext incoming {@link SecurityContext}, optional in use internally
     * @return PageAttributes object
     */
    PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext);
}
