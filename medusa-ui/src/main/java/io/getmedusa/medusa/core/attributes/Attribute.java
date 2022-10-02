package io.getmedusa.medusa.core.attributes;

public record Attribute(String name, Object value) {

    /** Attribute that indicates that loading is done. */
    public static final Attribute LOADING_DONE = new Attribute(StandardAttributeKeys.LOADING, "loading-done");
    public Attribute() {
        this(null, null);
    }

}
