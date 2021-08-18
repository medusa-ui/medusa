package com.sample.medusa.eventhandler.integrationtests;

import io.getmedusa.medusa.core.annotation.PageAttributes;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Arrays;
import java.util.List;
//localhost:8080/test/upper/selenium/normal/test?value=12&person=2
@UIEventPage(path ="/test/upper/{up}/normal/{nrml}", file = "pages/integration-tests/path-query-params")
public class PathQueryParamsEventHandler {

    final PeopleService service;

    public PathQueryParamsEventHandler(PeopleService service) {
        this.service = service;
    }

    public PageAttributes setupAttributes(ServerRequest request) {
        System.out.println(request.queryParams());
        System.out.println(service.findById("2").getName());
        return new PageAttributes()
                .with("path-to-upper", request.pathVariable("up"), String::toUpperCase)
                .with("path-as-is", request.pathVariable("nrml"))
                .with("query-as-is", request.queryParam("value").orElse("nothing"))
                .with("query-person", request.queryParam("person").orElse("1"), service::findById);
    }
}

@Service
class PeopleService {
    List<Person> people = Arrays.asList(new Person("1","메두사"), new Person("2","美杜莎"));

    Person findById(String id) {
        return people.stream().filter( person -> person.id.equals(id)).findFirst().get();
    }

}

class Person {
    String id;
    String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

