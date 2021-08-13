package io.getmedusa.medusa.core.registry;


import io.getmedusa.medusa.core.util.SessionToHash;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Keeps a singleton instance with all page titles and their page hashes.
 * As such we can quickly retrieve the unparsed page title and review if they are impacted by variable changes
 * Gets instantiated and filled up with values during beforeInitialization post-processing, like most registries.
 */
public class PageTitleRegistry {

    private static final PageTitleRegistry INSTANCE = new PageTitleRegistry();
    private static final Pattern PATTERN = Pattern.compile("<title>.*\\[\\$.+].*</title>", Pattern.CASE_INSENSITIVE);

    public static PageTitleRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, String> registry = new HashMap<>();

    public void addTitle(String hash, String htmlString) {
        if(!registry.containsKey(hash)) {
            Matcher matcher = PATTERN.matcher(htmlString);
            if (matcher.find()) {
                final String titleTag = matcher.group(0).replaceFirst("<title>", "").replaceFirst("</title>", "");

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
