package io.getmedusa.medusa.core.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link PageAttributes} is the wrapper for the {@link HashMap} which contains the initial variables for a page.
 * These variables can be different per ServerRequest.
 */
public class PageAttributes {

    private final Map<String, Object> pageVariables = new HashMap<>();

    @FunctionalInterface
    public interface Converter {
        Object convert(String in);
    }

    public PageAttributes(){}

    public PageAttributes(Map<String, Object> pageVariables) {
        if(null != pageVariables) this.pageVariables.putAll(pageVariables);
    }

    /**
     * Appends a value to the initial variables with a specific key
     * <p><i>Example</i>:
     * <pre>{@code
     *      new PageAttributes()
     *                 .with("path-to-upper", request.pathVariable("up"), String::toUpperCase)
     *                 .with("query-person", request.queryParam("number").orElse("1"), service::findById)
     * }</pre></p>
     * @param key   a key to identify the variable in HTML with
     * @param value the {@link String} value of the variable, which will be processed by the converter before being added
     * @param converter {@link Converter} function that will execute with the value as parameter first before storing it
     * @return PageAttributes with appended value
     */
    public PageAttributes with(String key, String value, Converter converter) {
        pageVariables.put(key, converter.convert(value));
        return this;
    }

    /**
     * Appends a value to the initial variables with a specific key
     * <p><i>Example</i>:
     * <pre>{@code
     *      new PageAttributes()
     *                 .with("example", "value-1")
     *                 .with("example-2", "value-2")
     * }</pre></p>
     * @param key   a key to identify the variable in HTML with
     * @param value the actual object value of the variable
     * @return PageAttributes with appended value
     */
    public PageAttributes with(String key, Object value) {
        pageVariables.put(key, value);
        return this;
    }

    public Map<String, Object> getPageVariables() {
        return pageVariables;
    }
}
