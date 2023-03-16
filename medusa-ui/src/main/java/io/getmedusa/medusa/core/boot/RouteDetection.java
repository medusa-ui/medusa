package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.util.FileUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a singleton instance with all routes and their respective HTML files, as set up by {@link UIEventPage}
 * This data is unique to this instance, and this is held in-memory only.
 * This is mostly used for initial route setup and hydra communications
 * Gets instantiated and filled up with values during beforeInitialization post-processing, like most registries.
 */
public enum RouteDetection {

    INSTANCE;

    private final Map<String, Route> detectedRoutes = new HashMap<>();

    public Collection<Route> getDetectedRoutes() {
        return detectedRoutes.values();
    }

    public void consider(Object bean) {
        final UIEventPage annotation = retrieveAnnotation(bean);
        if(null != annotation) {
            final String rawHTML = FileUtils.load(annotation.file());
            final String fragmentDetectedHTML = FragmentDetection.INSTANCE.prepFile(rawHTML);
            FormDetection.INSTANCE.prepFile(fragmentDetectedHTML, bean);
            final String staticResourceDetectedHTML = StaticResourcesDetection.INSTANCE.detectUsedResources(fragmentDetectedHTML);
            Route route = new Route(
                    annotation.path(),
                    staticResourceDetectedHTML,
                    bean);
            detectedRoutes.put(route.generateHash(), route);
        }
    }

    public Route findRoute(String key) {
        return detectedRoutes.getOrDefault(key, null);
    }

    private UIEventPage retrieveAnnotation(Object bean) {
        return bean.getClass().getAnnotation(UIEventPage.class);
    }
}
