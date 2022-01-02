package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/test/maps", file = "pages/integration-tests/maps.html")
public class MapsHandler {

    public PageAttributes setupAttributes() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("person", new Person("", "John"));
        modelMap.put("my-map", Map.of("a", 1, "b", 2));
        modelMap.put("my-array", new String[] { "a1", "b2" });
        return new PageAttributes(modelMap);
    }

    public DOMChanges updateMap() {
        return of("my-map", Map.of("a", 5, "b", 6));
    }

    public DOMChanges updateArray() {
        return of("my-array", new String[] { "a5", "b6" });
    }

    public DOMChanges updatePerson() {
        return of("person", new Person("", UUID.randomUUID().toString()));
    }
}
