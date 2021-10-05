package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.websocket.HydraMenuItem;

import java.util.*;


/**
 * Keeps a singleton instance with all routes and their respective HTML files, as set up by {@link io.getmedusa.medusa.core.annotation.UIEventPage}
 * This is mostly used for initial route setup and hydra communications
 * Gets instantiated and filled up with values during beforeInitialization post-processing, like most registries.
 */
public class RouteRegistry {

    private static final RouteRegistry INSTANCE = new RouteRegistry();

    public static RouteRegistry getInstance() {
        return INSTANCE;
    }
    private final Map<String, String> routesWithHTMLFile = new HashMap<>();
    private final Map<String, List<HydraMenuItem>> menuItems = new HashMap<>();

    public void add(String getPath, String htmlFile) {
        routesWithHTMLFile.put(getPath, htmlFile);
    }

    public void addMenuItem(String menuName, String label, String getPath) {
        HydraMenuItem menuItem = new HydraMenuItem(getPath, label);
        menuItems.computeIfAbsent(menuName, k -> new ArrayList<>()).add(menuItem);
    }

    public Set<Map.Entry<String, String>> getRoutesWithHTMLFile() {
        return routesWithHTMLFile.entrySet();
    }

    public Set<String> getRoutes() {
        return routesWithHTMLFile.keySet();
    }

    public Set<String> getWebSockets() {
        return new HashSet<>(routesWithHTMLFile.values());
    }

    public Map<String, List<HydraMenuItem>> getMenuItems() {
        return menuItems;
    }
}
