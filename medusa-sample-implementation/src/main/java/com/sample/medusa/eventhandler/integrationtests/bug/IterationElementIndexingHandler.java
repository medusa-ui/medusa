package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;
import java.util.Map;

@UIEventPage(path="/test/bug/iteration-indexing", file = "pages/integration-tests/bug/index-by-iteration")
public class IterationElementIndexingHandler {

    List<String> listOfString = List.of("A", "B", "C");
    List<ObjectWithName> listOfObjects =
            List.of(new ObjectWithName("A"), new ObjectWithName("B"), new ObjectWithName("C"));
    Map<String, String> map = Map.of("A", "one", "B", "two", "C", "three");

    public PageAttributes setupAttributes(ServerRequest request) {
        return new PageAttributes()
                .with("list-of-strings", listOfString)
                .with("list-of-objects", listOfObjects)
                .with("map", map);
    }

}

class ObjectWithName {
    String name;
    public ObjectWithName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}