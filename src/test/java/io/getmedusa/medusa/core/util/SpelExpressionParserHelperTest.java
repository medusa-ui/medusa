package io.getmedusa.medusa.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SpelExpressionParserHelperTest {

    @Test
    void escaping() {
        String raw = "search('안녕하세요세계hello%20world%27tasdasdsasdx', 3, 'text', 'term')";
        String escaped = ExpressionEval.escape(raw);
        String result = SpelExpressionParserHelper.getValue(escaped, new Person("John", 3, new Country("Belgium")));
        Assertions.assertEquals("hello medusa 123 :: 안녕하세요세계hello world'tasdasdsasdx", result);
    }

    @Test
    void basics() {
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
    void objectPropertiesAsString(){
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
    void objectProperties(){
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

    public String search(String parameter, int number, String type, String name) {
        return "hello medusa 123 :: " + parameter;
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