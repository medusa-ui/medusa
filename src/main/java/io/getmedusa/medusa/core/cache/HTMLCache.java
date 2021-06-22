package io.getmedusa.medusa.core.cache;

import java.util.HashMap;
import java.util.Map;

public class HTMLCache {

    private static final HTMLCache INSTANCE = new HTMLCache();

    public static HTMLCache getInstance() {
        return INSTANCE;
    }

    private static final Map<String, String> CACHE = new HashMap<>();

    public String getHTMLOrAdd(String filename, String html) {
        return CACHE.computeIfAbsent(filename, k -> html);
    }

    public String getHTML(String filename) {
        return CACHE.get(filename);
    }
}
