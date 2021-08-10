package com.sample.medusa.eventhandler.integrationtests.setup;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/test/setup/secure", file = "pages/integration-tests/custom-setup", setup = "custom")
public class CustomSetupSecureEventHandler {

    public PageAttributes custom(SecurityContext context) {
        return new PageAttributes()
                .with("message","PageAttributes custom(SecurityContext context)")
                .with("counter" , 0)
                .with("principal", context == null ? "guest" : context.getPrincipal().getName());
    }

    public DOMChanges increaseCounter(String counterValue, int parameter) {
        Integer counter = Integer.valueOf(counterValue) + parameter;
        return of("counter", counter);
    }

}
