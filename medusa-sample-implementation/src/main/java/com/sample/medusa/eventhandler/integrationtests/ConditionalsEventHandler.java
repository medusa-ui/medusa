package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@Component
public class ConditionalsEventHandler implements UIEventController {

    int counter = 0;

    @Override
    public PageSetup setupPage() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("counter-value", counter);
        return new PageSetup(
                "/test/conditionals",
                "pages/integration-tests/conditionals.html",
                modelMap);
    }

    public DOMChanges increaseCounter(int parameter) {
        counter += parameter;
        if(counter > 10) {
            counter = 0;
        }
        return of("counter-value", counter);
    }
}
