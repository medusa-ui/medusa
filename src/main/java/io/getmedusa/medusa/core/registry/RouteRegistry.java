package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.annotation.PageSetup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RouteRegistry {

    private static final RouteRegistry INSTANCE = new RouteRegistry();

    public static RouteRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, PageSetup> pageSetups = new HashMap<>();

    public void add(PageSetup pageSetup) {
        pageSetups.put(pageSetup.getGetPath(), pageSetup);
    }

    public PageSetup getPageSetupFromPath(String path) {
        return pageSetups.get(path);
    }

    public Collection<PageSetup> getAllPageSetups() {
        return pageSetups.values();
    }

    public Map<String, Object> getVariables(String getPath) {
        final PageSetup pageSetup = pageSetups.get(getPath);
        if(null == pageSetup) return new HashMap<>();
        return pageSetup.getPageVariables();
    }
}
