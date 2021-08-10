package com.sample.medusa.eventhandler.integrationtests.setup;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/test/setup/default", file = "pages/integration-tests/custom-setup")
public class DefaultSetupEventHandler {

    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("counter", 0);
        return new PageAttributes(modelMap);
    }

    public DOMChanges increaseCounter(String counterValue, int parameter) {
        Integer counter = (counterValue == null || counterValue.equals("counter")) ? 0 : Integer.valueOf(counterValue);
        return of("counter", counter + parameter);
    }
}
