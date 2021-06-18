package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.annotation.PageSetup;
import io.getmedusa.medusa.core.util.FilenameHandler;

import java.util.*;

public class RouteRegistry {

    private static final RouteRegistry INSTANCE = new RouteRegistry();

    public static RouteRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, PageSetup> pageSetups = new HashMap<>();

    public void add(PageSetup pageSetup) {
        pageSetups.put(pageSetup.getHtmlFile(), pageSetup);
    }

    public Collection<PageSetup> getAllPageSetups() {
        return pageSetups.values();
    }

    public Map<String, Object> getVariables(String filename) {
        final PageSetup pageSetup = pageSetups.get(FilenameHandler.removeExtension(filename));
        if(null == pageSetup) return new HashMap<>();
        return pageSetup.getPageVariables();
    }
}
