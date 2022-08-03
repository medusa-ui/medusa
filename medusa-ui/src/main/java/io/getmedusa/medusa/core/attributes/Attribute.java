package io.getmedusa.medusa.core.attributes;

public record Attribute(String name, Object value) {

    public Attribute() {
        this(null, null);
    }

}
