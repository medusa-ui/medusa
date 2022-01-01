package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.injector.tag.each.EachMemoryPerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a singleton instance with all iteration conditions and their iteration id
 * As such we can do a lookup if certain variable change would have impact on a condition,
 * and by consequence require re-interpretation of the iteration itself.
 * Gets instantiated and filled up with values during beforeInitialization post-processing, like most registries.
 */
public class EachValueRegistry {

    private static final EachValueRegistry INSTANCE = new EachValueRegistry();
    public static EachValueRegistry getInstance() {
        return INSTANCE;
    }
    private final Map<ServerRequest, EachMemoryPerRequest> registry = new HashMap<>();

    public void add(ServerRequest request, String eachName, int index, Object object) {
        EachMemoryPerRequest eachMemoryPerRequest = registry.getOrDefault(request, new EachMemoryPerRequest());
        eachMemoryPerRequest.add(eachName, index, object);
        registry.put(request, eachMemoryPerRequest);
    }

    public void clear(ServerRequest request) {
        registry.remove(request);
    }

    public Object get(ServerRequest request, String eachName, int index) {
        EachMemoryPerRequest eachMemoryPerRequest = registry.get(request);
        if(eachMemoryPerRequest == null) return null;
        return eachMemoryPerRequest.get(eachName, index);
    }
}
