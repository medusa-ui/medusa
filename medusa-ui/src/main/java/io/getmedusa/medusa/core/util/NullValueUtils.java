package io.getmedusa.medusa.core.util;

public class NullValueUtils {

    public static final String SIMPLE_NULL_REPRESENTATION = "undefined";

    /**
     * Returns the given value if it is not null, otherwise returns a {@value #SIMPLE_NULL_REPRESENTATION}.
     * @param value the value to check
     * @return the value if not null, otherwise return the default value
     */
    public static Object defaultIfNull(Object value) {
        return value == null ? SIMPLE_NULL_REPRESENTATION: value;
    }

}