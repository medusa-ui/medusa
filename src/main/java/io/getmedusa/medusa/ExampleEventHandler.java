package io.getmedusa.medusa;

import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.annotation.UIEvent;
import io.getmedusa.medusa.core.injector.DOMChange;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExampleEventHandler implements UIEventController {

    private int counter = 0;

    @Override
    public PageSetup setupPage() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("counter-value", counter);
        modelMap.put("search", "initial value!");
        return new PageSetup(
                "/",
                "hello-world",
                modelMap);
    }

    @UIEvent
    public List<DOMChange> exampleEvent(List<String> parameters) {
        return null;
    }

    @UIEvent(value = "exampleEvent3")
    public List<DOMChange> exampleEvent2(List<String> parameters) {
        return null;
    }

    @UIEvent
    public List<DOMChange> increaseCounter(List<String> parameters) {
        String parameter = parameters.get(0);
        if (parameter == null) parameter = "0";

        counter += Integer.parseInt(parameter);
        if(counter > 10) {
            counter = 0;
        }

        return Collections.singletonList(new DOMChange("counter-value", Integer.toString(counter)));
    }

    @UIEvent("search")
    public List<DOMChange> searchExample(List<String> parameters) {
        return Collections.singletonList(new DOMChange("search", UUID.randomUUID().toString()));
    }
}
