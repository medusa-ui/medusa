package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

public interface UIEventController {
    PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext);
}
