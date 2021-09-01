package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UIEventPage(file = "pages/integration-tests/bug/nested-each.html", path = "/bug/nested-each")
public class BreakNestedEachEventHandler {

    List<Integer> outer = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
    List<String> inner = new ArrayList<>(Arrays.asList("A","B")); // bug is more obvious when the size of the inner loop is greater than the outer loop.(index out of bound exception)

    public PageAttributes setupAttributes() {
        return new PageAttributes()
                .with("inner", inner)
                .with("outer", outer);
    }

    public DOMChanges refresh() {
        return DOMChanges.of("inner", inner).and("outer", outer);
    }

    public DOMChanges add() {
        //inner.add(inner.size() + 1);
        outer.add(outer.size() + 1);
        return DOMChanges.of("inner", inner).and("outer", outer);
    }

    public DOMChanges remove() {
        inner.remove(0);
        outer.remove(0);
        return DOMChanges.of("inner", inner).and("outer", outer);
    }
}
