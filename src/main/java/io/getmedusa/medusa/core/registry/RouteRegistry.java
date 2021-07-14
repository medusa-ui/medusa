package io.getmedusa.medusa.core.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RouteRegistry {

    private static final RouteRegistry INSTANCE = new RouteRegistry();
    public static RouteRegistry getInstance() {
        return INSTANCE;
    }
    private final Map<String, String> routesWithHTMLFile = new HashMap<>();

    public void add(String getPath, String htmlFile) {
        routesWithHTMLFile.put(getPath, htmlFile);
    }

    public Set<Map.Entry<String, String>> getRoutesWithHTMLFile() {
        return routesWithHTMLFile.entrySet();
    }

    public Set<String> getRoutes() {
        return routesWithHTMLFile.keySet();
    }
}
