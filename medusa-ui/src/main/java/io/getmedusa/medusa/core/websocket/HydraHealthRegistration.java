package io.getmedusa.medusa.core.websocket;

import java.util.*;

public class HydraHealthRegistration {

    private final int port;
    private final String name;
    private Set<String> endpoints = new HashSet<>();
    private Set<String> websockets = new HashSet<>();
    private Set<String> staticResources = new HashSet<>();
    private Map<String, List<HydraMenuItem>> menuItems = new HashMap<>();

    public HydraHealthRegistration(int port, String name) {
        this.port = port;
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public Set<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Set<String> endpoints) {
        this.endpoints = endpoints;
    }

    public Set<String> getWebsockets() {
        return websockets;
    }

    public void setWebsockets(Set<String> websockets) {
        this.websockets = websockets;
    }

    public Set<String> getStaticResources() {
        return staticResources;
    }

    public void setStaticResources(Set<String> staticResources) {
        this.staticResources = staticResources;
    }

    public Map<String, List<HydraMenuItem>> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Map<String, List<HydraMenuItem>> menuItems) {
        this.menuItems = menuItems;
    }
}
