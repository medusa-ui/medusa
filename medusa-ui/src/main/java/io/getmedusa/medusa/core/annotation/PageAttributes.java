package io.getmedusa.medusa.core.annotation;

import java.util.HashMap;
import java.util.Map;

public class PageAttributes {

    private final Map<String, Object> pageVariables = new HashMap<>();

    public PageAttributes(Map<String, Object> pageVariables) {
        if(null != pageVariables) this.pageVariables.putAll(pageVariables);
    }

    public Map<String, Object> getPageVariables() {
        return pageVariables;
    }
}
