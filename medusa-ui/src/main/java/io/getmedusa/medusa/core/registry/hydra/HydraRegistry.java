package io.getmedusa.medusa.core.registry.hydra;

import java.util.HashMap;
import java.util.Map;

public enum HydraRegistry {

    INSTANCE;

    private final Map<String, String> otherRoutes = new HashMap<>();

    public String lookupRoute(String linkReference) {
        return otherRoutes.get(linkReference);
    }

    public void addRoute(String linkReference, String actualRoute) {
        otherRoutes.put(linkReference, actualRoute);
    }

    private void addRoute(KnownRoute knownRoute) {
        System.out.println(knownRoute);
        for(String route : knownRoute.getAvailableRoutes()) {
            addRoute(knownRoute.getService(), route);
        }
    }

    public void addRoutes(KnownRoutes knownRoutes) {
        for(KnownRoute knownRoute : knownRoutes.getKnownRoutes()) {
            addRoute(knownRoute);
        }
    }

    public void clear() {
        otherRoutes.clear();
    }
}
