package io.getmedusa.medusa.core.registry.hydra;

import java.util.HashMap;
import java.util.Map;

public enum HydraRegistry {

    INSTANCE;

    private final Map<String, String> otherRoutes = new HashMap<>();

    public String lookupRoute(String tagContent) {
        return "/xyz";
    }
}
