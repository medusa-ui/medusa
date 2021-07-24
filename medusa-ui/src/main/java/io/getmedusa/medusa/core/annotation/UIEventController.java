package io.getmedusa.medusa.core.annotation;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

public interface UIEventController {
    PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext);
}
