package io.getmedusa.medusa.core.router;

import io.getmedusa.medusa.core.registry.RouteRegistry;
import io.getmedusa.medusa.core.websocket.EventOptionalParams;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(ReactiveSecurityContextHolder.class)
public class SecuritySetup {

    public SecuritySetup() {
        EventOptionalParams.securityEnabled = true;
        RouteRegistry.getInstance().add("/login", "pages/login");
    }

}
