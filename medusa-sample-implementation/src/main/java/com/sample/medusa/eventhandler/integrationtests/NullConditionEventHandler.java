package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;

@UIEventPage(path = "/null-condition", file = "pages/integration-tests/null-condition.html")
public class NullConditionEventHandler {

    public Person person;

    public PageAttributes setupAttributes() {
        return new PageAttributes().with("person", person);
    }

    public DOMChanges someOne(){
        person = new Person("doe.j", "Jhon Doe");
        return DOMChanges.of("person", person);
    }

    public DOMChanges noOne() {
        person = null;
        return DOMChanges.of("person", person);
    }

    public DOMChanges someNullName(){
        person = new Person("no.name", null);
        return DOMChanges.of("person", person);
    }
}
