package io.getmedusa.medusa.core.registry;


import io.getmedusa.medusa.core.util.SessionToHTMLFileName;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageTitleRegistry {

    private static final PageTitleRegistry INSTANCE = new PageTitleRegistry();
    private static final Pattern PATTERN = Pattern.compile("<title>.*\\[\\$.+].*</title>", Pattern.CASE_INSENSITIVE);

    public static PageTitleRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, String> registry = new HashMap<>();

    public void addTitle(String fileName, String htmlString) {
        if(!registry.containsKey(fileName)) {
            Matcher matcher = PATTERN.matcher(htmlString);
            if (matcher.find()) {
                final String titleTag = matcher.group(0).replaceFirst("<title>", "").replaceFirst("</title>", "");

                this.registry.put(fileName, titleTag);
            }
        }
    }

    public String getTitle(WebSocketSession session) {
        return registry.get(SessionToHTMLFileName.parse(session));
    }

    public void clear() {
        registry.clear();
    }
}
