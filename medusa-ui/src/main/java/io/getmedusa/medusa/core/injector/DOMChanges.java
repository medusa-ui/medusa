package io.getmedusa.medusa.core.injector;

import java.util.ArrayList;
import java.util.List;

/**
 * DOMChanges are variables used in HTML that have changed. It allows you to specify specifically what has changed and let the HTML DOM react to it. <br/>
 * This class is a wrapper for a list of {@link DOMChange} elements. It provides convenience functions to easily add key value pairs to said list.
 * <pre>{@code
 *      DOMChanges.of("variable-1", var1)
 *                .and("variable-2", var2)
 *                .and("variable-3", var3)
 * }</pre></p>
 * The keys are expected to match the variable names used in HTML and in initial attribute setup.
 *
 */
public class DOMChanges {

    private final List<DOMChange> domChanges = new ArrayList<>();

    private DOMChanges() {}

    /**
     * Constructor for a single key/value pair
     * Consider using DOMChanges.of(), which allows you to subsequently chain .and() calls.
     * @param field key, matching with variable name used in HTML and setupAttributes()
     * @param value value of said variable
     */
    public DOMChanges(String field, Object value) {
        domChanges.add(new DOMChange(field, value));
    }

    /**
     * Constructor for a single key/value pair, with a specific DOMChangeType. Generally not used directly.
     * Consider using DOMChanges.of(), which allows you to subsequently chain .and() calls.
     * @param field key, matching with variable name used in HTML and setupAttributes()
     * @param value value of said variable
     * @param type specific internal type of the DOMChange. Generally left up to internal implementation.
     */
    public DOMChanges(String field, Object value, DOMChange.DOMChangeType type) {
        domChanges.add(new DOMChange(field, value, type));
    }

    /**
     * Convenience method for empty DOMChanges object. <br/>
     * If you are using this as the result of an event function, consider making the event method <i>void</i> instead, which is legal and more efficient.
     * @return A new empty DOMChanges object
     */
    public static DOMChanges empty() {
        return new DOMChanges();
    }

    /**
     * Adds a key/value pair to an existing DOMChanges object.
     * @param field key, matching with variable name used in HTML and setupAttributes()
     * @param value value of said variable
     * @return The appended DOMChanges object
     */
    public DOMChanges and(String field, Object value) {
        domChanges.add(new DOMChange(field, value));
        return this;
    }

    /**
     * Adds a key/value pair to an existing DOMChanges object, but with an explicit DOMChangeType. Generally left up to internal implementations.
     * @param field key, matching with variable name used in HTML and setupAttributes()
     * @param value value of said variable
     * @param type specific internal type of the DOMChange. Generally left up to internal implementation.
     * @return The appended DOMChanges object
     */
    public DOMChanges and(String field, Object value, DOMChange.DOMChangeType type) {
        domChanges.add(new DOMChange(field, value, type));
        return this;
    }

    /**
     * Preferred way of creating a new chain of key/value pairs.
     * @param field key, matching with variable name used in HTML and setupAttributes()
     * @param value value of said variable
     * @return A new DOMChanges object, ready for chaining with .and()
     */
    public static DOMChanges of(String field, Object value) {
        return new DOMChanges(field, value);
    }

    /**
     * Creates a new chain of key/value pairs, but with an explicit DOMChangeType. Generally left up to internal implementations.
     * @param field key, matching with variable name used in HTML and setupAttributes()
     * @param value value of said variable
     * @param type specific internal type of the DOMChange. Generally left up to internal implementation.
     * @return A new DOMChanges object, ready for chaining with .and()
     */
    public static DOMChanges of(String field, Object value, DOMChange.DOMChangeType type) {
        return new DOMChanges(field, value, type);
    }

    /**
     * Returns the list of {@link DOMChange}s up to that point
     * @return the list of {@link DOMChange}s
     */
    public List<DOMChange> build() {
        return domChanges;
    }

    public static class DOMChange {

        private String f; //field
        private Object v; //value (value or relevant id)
        private Integer t; //type
        private String c; //condition

        public DOMChange(String field, Object value) {
            this.f = field;
            this.v = value;
        }

        public DOMChange(String field, Object value, DOMChange.DOMChangeType type) {
            this.f = field;
            this.v = value;
            this.t = type.ordinal();
        }

        public String getF() {
            return f;
        }

        public void setF(String f) {
            this.f = f;
        }

        public Object getV() {
            return v;
        }

        public void setV(Object v) {
            this.v = v;
        }

        public Integer getT() {
            return t;
        }

        public void setT(Integer t) {
            this.t = t;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public enum DOMChangeType {
            TITLE,
            CONDITION,
            ITERATION,
            CONDITIONAL_CLASS,
            M_ATTR
        }
    }
}
