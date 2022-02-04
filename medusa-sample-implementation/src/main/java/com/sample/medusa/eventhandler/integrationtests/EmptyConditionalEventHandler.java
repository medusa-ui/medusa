package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

@UIEventPage(file = "pages/integration-tests/empty.html", path = "/test/conditional-empty")
public class EmptyConditionalEventHandler {

    // empty
    List<String> list = new ArrayList<>();
    Set<String> set = new HashSet<>();
    Map<String,String> map = new HashMap<>();
    String text = "";

    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {
        return new PageAttributes()
                .with("text",text)
                .with("list", list)
                .with("set", set)
                .with("map", map);
    }

    public DOMChanges values(){
        text = "some text";
        list.add("a list item");
        set.add("a set item");
        map.put("a key","a map value");
        return DOMChanges.of("text",text)
                .and("list", list)
                .and("set", set)
                .and("map", map);
    }

    public DOMChanges clear(){
        text = "";
        list.clear();
        set.clear();
        map.clear();
        return DOMChanges.of("text",text)
                .and("list", list)
                .and("set", set)
                .and("map", map);
    }

}
