package io.getmedusa.medusa.core.attributes;

import java.util.LinkedList;
import java.util.List;

public record Attribute(String name, Object value) {

    /** Attribute that indicates that loading is done. */
    public static final Attribute LOADING_DONE = new Attribute(StandardAttributeKeys.LOADING, "loading-done");
    public Attribute() {
        this(null, null);
    }

    public static Attribute $(String name, Object value) {
        return new Attribute(name, value);
    }

    public static List<Attribute> $$(Object... keyValuePairs) {
        if(keyValuePairs == null || keyValuePairs.length == 0) { return new LinkedList<>(); }

        List<Attribute> attributes = new LinkedList<>();

        if(keyValuePairs[0] instanceof Attribute) {
            for(Object obj : keyValuePairs) {
                if(obj instanceof Attribute a) {
                    attributes.add(a);
                } else {
                    throw new IllegalArgumentException("Don't combine Attribute calls with generic Object key/value pairs. Choose one or the other.");
                }
            }
        } else {
            if ((keyValuePairs.length & 1) != 0) {
                throw new IllegalArgumentException("Must provide key/value pairs, so this call cannot have uneven parameters");
            }

            for (int i = 0; i < keyValuePairs.length; i+=2) {
                attributes.add($(keyValuePairs[i].toString(), keyValuePairs[i+1]));
            }
        }

        return attributes;
    }
}
