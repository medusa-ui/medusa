package io.getmedusa.medusa.core;

import io.getmedusa.medusa.core.util.PropertyAccessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;

public class PropertyAccessorTest {

    @Test
    public void basics(){

        String value = PropertyAccessor.getValue("41 + 1");
        Assertions.assertEquals("42", value);

        String year = PropertyAccessor.getValue("T(java.time.Year).now()");
        Assertions.assertEquals(Year.now().toString(),year);
    }

    @Test
    public void objectPropertiesAsString(){
        // given
        Person kevin = new Person("Kevin",30, new Country("USA"));
        Person dirk = new Person("Dirk",55, new Country("Belgium"));

        // then
        String age = PropertyAccessor.getValue("age", kevin);
        Assertions.assertEquals("30", age);

        // and
        String location = PropertyAccessor.getValue("location.name", dirk);
        Assertions.assertEquals("Belgium", location);
    }

    @Test
    public void objectProperties(){
        // given
        Country usa = new Country("USA");
        Person kevin = new Person("Kevin",30, usa);

        // then
        Country location = PropertyAccessor.getValue("location", kevin, Country.class);
        Assertions.assertEquals(usa, location);
    }


}

class Person {
    Country location;
    String name;
    int age;

    public Person(String name, int age, Country location) {
        this.name = name;
        this.age = age;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Country getLocation() {
        return location;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return name + ", " +  age + " years old, living in " + location;
    }
}

class Country{

    String name;

    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        return name.equals(country.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}