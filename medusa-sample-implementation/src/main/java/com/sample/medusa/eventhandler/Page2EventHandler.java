package com.sample.medusa.eventhandler;

import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChange;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Page2EventHandler implements UIEventController {

    @Override
    public PageSetup setupPage() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("example-value", UUID.randomUUID().toString());
        return new PageSetup(
                "/page2",
                "page2",
                modelMap);
    }

    //second method with same signature to check if page switching works correctly
    public DOMChange.DOMChanges buy(Object... parameters) {
        return DOMChange.of("example-value", "this store is closed!");
    }

}
