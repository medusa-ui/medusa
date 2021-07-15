package io.getmedusa.medusa;

import io.getmedusa.medusa.core.annotation.DOMChanges;
import io.getmedusa.medusa.core.annotation.IgnoreDOMChanges;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class ExampleEventPage implements UIEventPage {

    Set<Person> people = new HashSet<>(Arrays.asList(new Person("Dirk",55), new Person("Kevin",30)));
    int counter = 5;

    public int getCounter() {
        return counter;
    }

    @DOMChanges({ "counter", "calculated", "aboveEight" })
    public void increase(int value) {
        counter++;
    }

    @DOMChanges({"counter", "aboveEight" }) // here we ignore changes in calculated and counterAboveEight
    public void decrease(int value) {
        counter--;
    }

    @DOMChanges({"people", "averageAge"}) // here we ignore changes in calculated
    public void refreshPeople() {
        people.add(new Person("Nicole", 55));
    }

    public int getCalculated(){
        return 13 * counter;
    }

    public Set<Person> getPeople() {
        return people;
    }

    public int getAverageAge() {
        int total = 0;
        for(Person person : people) {
            total += person.getAge();
        }
        return total/ people.size();
    }

    public boolean isAboveEight() {
        return counter > 8;
    }

    @IgnoreDOMChanges // getters can be ignored and do not show up in attributes
    public ExampleEventPage getExampleEventPage(){
        return this;
    }

    //maybe we could use @UIEventPage(path="/ui-event-page", page="") instead of an interface?
    @Override
    public String path() {
        return "/counter";
    }
    @Override
    public String htmlFile() {
        return "ui-event-page";
    }
}

class Person {
    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}