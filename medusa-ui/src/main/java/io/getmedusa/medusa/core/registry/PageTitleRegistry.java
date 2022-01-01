package io.getmedusa.medusa.core.registry;


import io.getmedusa.medusa.core.util.SessionToHash;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a singleton instance with all page titles and their page hashes.
 * As such we can quickly retrieve the unparsed page title and review if they are impacted by variable changes
 * Gets instantiated and filled up with values during beforeInitialization post-processing, like most registries.
 */
public class PageTitleRegistry {

    private static final PageTitleRegistry INSTANCE = new PageTitleRegistry();

    public static PageTitleRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, String> registry = new HashMap<>();

    public void addTitle(String hash, Document document) {
        if(!registry.containsKey(hash)) {
            Elements potentialTags = document.getElementsByTag("title");
            if(potentialTags.size() == 1) {
                final String titleTag = potentialTags.get(0).text();
                this.registry.put(hash, titleTag);
            }
        }
    }

    public String getTitle(WebSocketSession session) {
        return registry.get(SessionToHash.parse(session));
    }

    public void clear() {
        registry.clear();
    }

    public boolean hasTitle(String fileName) {
        return registry.containsKey(fileName);
    }
}
