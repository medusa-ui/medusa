package io.getmedusa.medusa.core.registry.hydra;

import java.util.List;

public class KnownRoute {

    private String service;
    private List<String> availableRoutes;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public List<String> getAvailableRoutes() {
        return availableRoutes;
    }

    public void setAvailableRoutes(List<String> availableRoutes) {
        this.availableRoutes = availableRoutes;
    }

    @Override
    public String toString() {
        return "KnownRoute{" +
                "service='" + service + '\'' +
                ", availableRoutes=" + availableRoutes +
                '}';
    }
}
