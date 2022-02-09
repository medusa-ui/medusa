package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import io.getmedusa.medusa.core.util.SecurityContext;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

@UIEventPage(file = "pages/integration-tests/empty.html", path = "/test/conditional-empty/{defaultState}")
public class EmptyConditionalEventHandler {

    // empty
    List<String> list = new ArrayList<>();
    Set<String> set = new HashSet<>();
    Map<String,String> map = new HashMap<>();
    String text = "";
    Person person;

    public PageAttributes setupAttributes(ServerRequest request, SecurityContext securityContext) {

        int defaultState = Integer.parseInt(request.pathVariable("defaultState"));
        //0 = empty, not null, 1=null, 2=values
        switch (defaultState) {
            case 1 -> setToNull();
            case 2 -> values();
            default -> clear();
        }

        return new PageAttributes()
                .with("text",text)
                .with("list", list)
                .with("set", set)
                .with("map", map)
                .with("person",person);
    }

    public DOMChanges values(){
        text = "some text";
        if(list == null) list = new ArrayList<>();
        list.add("a list item");

        if(set == null) set = new HashSet<>();
        set.add("a set item");

        if(map == null) map = new HashMap<>();
        map.put("a key","a map value");

        person = new Person("jd","John Doe");

        return DOMChanges.of("text",text)
                .and("list", list)
                .and("set", set)
                .and("map", map)
                .and("person", person);
    }

    public DOMChanges clear(){
        text = "";

        if(list == null) list = new ArrayList<>();
        list.clear();

        if(set == null) set = new HashSet<>();
        set.clear();

        if(map == null) map = new HashMap<>();
        map.clear();

        person = new Person("jd",null);

        return DOMChanges.of("text",text)
                .and("list", list)
                .and("set", set)
                .and("map", map)
                .and("person", person);
    }

    public DOMChanges setToNull() {
        text = null;
        list = null;
        set = null;
        map = null;
        person = null;

        return DOMChanges.of("text",text)
                .and("list", list)
                .and("set", set)
                .and("map", map)
                .and("person", person);
    }
}
