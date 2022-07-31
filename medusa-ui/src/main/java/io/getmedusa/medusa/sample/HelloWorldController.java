package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.bidirectional.ServerToClient;
import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.session.StandardSessionTagKeys;
import io.getmedusa.medusa.core.session.StandardSessionTagValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@UIEventPage(path = "/", file = "/pages/hello-world")
public class HelloWorldController {

    private int counter;

    private List<Person> globalPeople = new ArrayList<>();

    @Autowired
    private ServerToClient serverToClient;

    //@Scheduled(fixedDelay = 1000)
    public void updateCounterFromServer() {
        serverToClient.sendAttributesToSessionTag(List.of(new Attribute("counterValue", ++counter)),
                StandardSessionTagKeys.ROUTE,
                StandardSessionTagValues.ALL); //TODO gotta improve this somehow
    }

    public List<Attribute> setupAttributes(ServerRequest serverRequest, Session session) {
        List<Person> people = new ArrayList<>();//generateListOfPeople();
        return List.of(
                //new Attribute("counterValue", Integer.parseInt(serverRequest.pathVariable("samplePathVariable"))),
                new Attribute("counterValue", 0),
                new Attribute("expectedTableCount", people.size()),
                new Attribute("people", people));
    }

    public List<Attribute> increaseCounter() {
        return List.of(new Attribute("counterValue", ++counter));
    }

    public List<Attribute> increaseCounterWith(Integer increaseWith) {
        this.counter += increaseWith;
        return List.of(new Attribute("counterValue", counter));
    }

    public List<Attribute> randomNewTable() {
        List<Person> people = generateListOfPeople();
        return List.of(
                new Attribute("counterValue", ++counter),
                new Attribute("expectedTableCount", people.size()),
                new Attribute("people", people));
    }

    public List<Attribute> addPerson() {
        globalPeople.add(new Person(globalPeople.size() + 1, new Date().getTime()));
        return List.of(
                new Attribute("expectedTableCount", globalPeople.size()),
                new Attribute("people", globalPeople));
    }

    public List<Attribute> removePerson() {
        globalPeople.remove(globalPeople.size()-1);
        return List.of(
                new Attribute("expectedTableCount", globalPeople.size()),
                new Attribute("people", globalPeople));
    }

    private List<Person> generateListOfPeople() {
        List<Person> people = new ArrayList<>();
        int amount = new SecureRandom().nextInt(3, 15);
        long timestamp = new Date().getTime();
        for (int i = 0; i < amount; i++) {
            people.add(new Person(i+1, timestamp));
        }
        globalPeople = people;
        return people;
    }

    /* test m:click & m:select */
    public List<Attribute> search(String searchFor) {
        return List.of(new Attribute("search", searchFor));
    }

}
