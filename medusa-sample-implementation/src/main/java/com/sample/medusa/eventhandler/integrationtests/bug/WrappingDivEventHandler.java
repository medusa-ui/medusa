package com.sample.medusa.eventhandler.integrationtests.bug;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.injector.DOMChanges;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

import static io.getmedusa.medusa.core.injector.DOMChanges.of;

@UIEventPage(path="/test/bug/js-wrapping-div", file = "pages/integration-tests/bug/wrapping-div")
public class WrappingDivEventHandler {

    public PageAttributes setupAttributes(ServerRequest request) {
        return new PageAttributes().with("people", List.of(new Person("A"), new Person("B")));
    }

    public DOMChanges render() {
        return of("people", List.of(new Person("C"), new Person("D")));
    }

    static class Person {
        private String name;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
