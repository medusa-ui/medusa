package com.sample.medusa.eventhandler;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.security.Principal;
import java.util.*;

@UIEventPage(path = "/page2", file = "pages/page2.html")
public class Page2EventHandler implements UIEventController {

    @Override
    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("example-value", UUID.randomUUID().toString());
        return new PageAttributes(modelMap);
    }

    //second method with same signature to check if page switching works correctly
    public DOMChanges buy(Object... parameters) {
        return DOMChanges.of("example-value", "this store is closed!");
    }

}
