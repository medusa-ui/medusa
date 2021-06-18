package io.getmedusa.medusa;

import io.getmedusa.medusa.core.annotation.UIEvent;
import io.getmedusa.medusa.core.annotation.UIEventController;
import io.getmedusa.medusa.core.injector.DOMChange;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@UIEventController
public class ExampleEventHandler {

    @UIEvent
    public List<DOMChange> exampleEvent(List<String> parameters) {
        return null;
    }

    @UIEvent
    public List<DOMChange> exampleEvent2(List<String> parameters) {
        return null;
    }

    private int counter = 0;

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

    @UIEvent
    public List<DOMChange> search(List<String> parameters) {
        return Arrays.asList(new DOMChange("search", UUID.randomUUID().toString()));
    }
}
