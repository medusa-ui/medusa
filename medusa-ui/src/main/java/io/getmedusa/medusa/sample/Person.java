package io.getmedusa.medusa.sample;

import java.security.SecureRandom;

public class Person {

    private final String name;
    private final int number;

    public Person(int i, long timestamp) {
        this.name = i + "_" + timestamp;
        this.number = new SecureRandom().nextInt(1, 10);
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
}
