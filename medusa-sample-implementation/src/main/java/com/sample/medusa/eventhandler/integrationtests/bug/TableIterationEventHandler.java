package com.sample.medusa.eventhandler.integrationtests.bug;


import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/table", file = "pages/integration-tests/bug/table-iteration")
public class TableIterationEventHandler {

    List<Item> items = new ArrayList<>();

    public PageAttributes setupAttributes(ServerRequest request) {
        items.clear();
        if(Boolean.valueOf(request.queryParam("load").orElse("false"))) {
            setData();
        }
        return new PageAttributes()
                .with("items",items);
    }

    public DOMChanges clear() {
        items.clear();
        return of("items", items);
    }

    public DOMChanges load() {
        setData();
        return of("items", items);
    }

    public DOMChanges add() {
        switch(items.size()) {
            case 0: items.add(new Item("zero","nul")); break;
            case 1: items.add(new Item("one","één")); break;
            case 2: items.add(new Item("two","twee")); break;
            case 3: items.add(new Item("three","drie")); break;
            case 4: items.add(new Item("four","vier")); break;
            default: items.add(new Item(UUID.randomUUID().toString(),UUID.randomUUID().toString())); break;
        }
        return of("items", items);
    }

    public void setData(){
        items.clear();
        items.add(new Item("zero","nul"));
        items.add(new Item("one","één"));
        items.add(new Item("two","twee"));
        items.add(new Item("three","drie"));
    }

}

class Item {
     String name;
     String value;

    public Item(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
