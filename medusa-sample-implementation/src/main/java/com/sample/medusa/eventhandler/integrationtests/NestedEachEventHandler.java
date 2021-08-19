package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UIEventPage(file = "pages/integration-tests/nested-each.html", path = "/nested-each")
public class NestedEachEventHandler {

    List<Integer> outer = new ArrayList<>(Arrays.asList(1,2,3));
    List<Integer> mid = new ArrayList<>(Arrays.asList(10, 20, 30));
    List<Integer> inner = new ArrayList<>(Arrays.asList(100, 200, 300));

    public PageAttributes setupAttributes() {
        return new PageAttributes()
                .with("outer", outer)
                .with("mid", mid)
                .with("inner",inner);
    }

    public DOMChanges first(){
        return DOMChanges.of("first", 1);
    }

}
