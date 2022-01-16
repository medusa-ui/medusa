package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.util.HashMap;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path = "/maps", file = "pages/integration-tests/maps.html")
public class MapsHandler {

    public PageAttributes setupAttributes() {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("person", new Person("", "John"));
        modelMap.put("my-map", Map.of("a", 1, "b", 2));
        modelMap.put("my-array", new String[] { "a1", "b2" });
        modelMap.put("mapClickValue", "% Not yet clicked %");
        modelMap.put("arrayClickValue", "% Not yet clicked %");
        return new PageAttributes(modelMap);
    }

    public DOMChanges updateMap() {
        return of("my-map", Map.of("a", 5, "b", 6));
    }

    public DOMChanges updateArray() {
        return of("my-array", new String[] { "a5", "b6" });
    }

    public DOMChanges updatePerson() {
        return of("person", new Person("", "Paul"));
    }

    public DOMChanges sampleClick(String passedValue, String type) {
        if("array".equals(type)) {
            return of("arrayClickValue", passedValue);
        } else {
            return of("mapClickValue", passedValue);
        }

    }
}
