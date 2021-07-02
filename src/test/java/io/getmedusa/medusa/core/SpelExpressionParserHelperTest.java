package io.getmedusa.medusa.core;

import io.getmedusa.medusa.core.util.SpelExpressionParserHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class SpelExpressionParserHelperTest {

    @Test
    public void basics() {
        // 42
        String value = SpelExpressionParserHelper.getStringValue("41 + 1");
        Assertions.assertEquals("42", value);

        Integer sum = SpelExpressionParserHelper.getValue("41 + 1");
        Assertions.assertEquals(42, sum);

        // current year
        String year = SpelExpressionParserHelper.getStringValue("T(java.time.Year).now()");
        Assertions.assertEquals(Year.now().toString(),year);

        Year currentYear = SpelExpressionParserHelper.getValue("T(java.time.Year).now()");
        Assertions.assertEquals(Year.now(), currentYear);

        // lists
        List empty = Collections.emptyList();
        Boolean isEmpty = SpelExpressionParserHelper.getValue("isEmpty()", empty);
        Integer size = SpelExpressionParserHelper.getValue("size()", empty);
        Assertions.assertTrue(isEmpty);
        Assertions.assertEquals(size, 0);

        List<Country> countries = Arrays.asList(new Country("Belgium"), new Country("USA"));
        isEmpty = SpelExpressionParserHelper.getValue("isEmpty()", countries);
        size = SpelExpressionParserHelper.getValue("size()", countries);
        Assertions.assertFalse(isEmpty);
        Assertions.assertEquals(size, 2);
    }

    @Test
    public void objectPropertiesAsString(){
        // given
        Person kevin = new Person("Kevin",30, new Country("USA"));
        Person dirk = new Person("Dirk",55, new Country("Belgium"));

        // then
        String age = SpelExpressionParserHelper.getStringValue("age", kevin);
        Assertions.assertEquals("30", age);

        // and
        String location = SpelExpressionParserHelper.getStringValue("location.name", dirk);
        Assertions.assertEquals("Belgium", location);
    }

    @Test
    public void objectProperties(){
        // given
        Country usa = new Country("USA");
        Person kevin = new Person("Kevin",30, usa);

        // then
        Country location = SpelExpressionParserHelper.getValue("location", kevin);
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