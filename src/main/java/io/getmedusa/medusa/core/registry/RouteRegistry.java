package io.getmedusa.medusa.core.registry;

import io.getmedusa.medusa.core.annotation.PageSetup;

import java.util.ArrayList;
import java.util.List;

public class RouteRegistry {

    private static final RouteRegistry INSTANCE = new RouteRegistry();

    public static RouteRegistry getInstance() {
        return INSTANCE;
    }

    private final List<PageSetup> pageSetups = new ArrayList<>();

    public void add(PageSetup pageSetup) {
        pageSetups.add(pageSetup);
    }

    public List<PageSetup> getAllPageSetups() {
        return pageSetups;
    }

    public void clear() {
        pageSetups.clear();
    }

}
