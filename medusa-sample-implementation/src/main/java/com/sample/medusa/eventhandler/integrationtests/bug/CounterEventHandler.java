package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/counter", file = "pages/integration-tests/bug/counter")
public class CounterEventHandler {
    Counter counter = new Counter();

    public PageAttributes setupAttributes() {
        return new PageAttributes().with("counter", counter);
    }

    public DOMChanges increment() {
        counter.increment();
        return of("counter", counter);
    }
}

class Counter {

    private Integer count;

    public Counter() {
        count = 0;
    }

    public void increment() {
        count++;
    }

    public Integer getValue() {
        return count;
    }
}