package io.getmedusa.medusa.core.annotation;

import java.util.HashMap;
import java.util.Map;

public class PageAttributes {

    private final Map<String, Object> pageVariables = new HashMap<>();

    @FunctionalInterface
    public interface Converter {
        Converter stringToString = in -> in;
        Object convert(String in);
    }

    public PageAttributes(){}

    public PageAttributes(Map<String, Object> pageVariables) {
        if(null != pageVariables) this.pageVariables.putAll(pageVariables);
    }

    public PageAttributes with(String key, String value, Converter converter) {
        pageVariables.put(key, converter.convert(value));
        return this;
    }

    public PageAttributes with(String key, Object value) {
        pageVariables.put(key, value);
        return this;
    }

    public Map<String, Object> getPageVariables() {
        return pageVariables;
    }
}
