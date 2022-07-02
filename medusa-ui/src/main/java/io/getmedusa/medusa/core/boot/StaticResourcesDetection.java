package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.session.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.*;

public enum StaticResourcesDetection {

    INSTANCE;

    private static final String HYDRA_PATH = "{hydrapath}";
    private Set<String> staticResourcesAvailable = new HashSet<>();
    private final Map<String, String> staticResourcesToReplace = buildWithDefaults();

    private Map<String, String> buildWithDefaults() {
        final Map<String, String> map = new HashMap<>();
        map.put("<script src=\"/websocket.js\"></script>", "<script src=\"/"+ HYDRA_PATH +"/websocket.js\"></script>");
        return map;
    }

    public void detectAvailableStaticResources(ResourcePatternResolver resourceResolver) {
        try {
            staticResourcesAvailable = determineListOfStaticResources(resourceResolver);
        } catch (IOException e) {
            throw new IllegalStateException("Could not detect static resources", e);
        }
    }

    private Set<String> determineListOfStaticResources(ResourcePatternResolver resourceResolver) throws IOException {
        Resource[] resources = resourceResolver.getResources("classpath:**/**.*");
        Set<String> set = new HashSet<>();
        for (Resource r : resources) {
            String path = r.getURI().getPath();
            if (path != null && path.contains("/static/") && !isPartOfStandardMedusaSetup(path)) {
                set.add(path.substring(path.indexOf("/static/") + 8));
            }
        }
        return set;
    }

    private boolean isPartOfStandardMedusaSetup(String path) {
        return path.contains("/node_modules/") ||
                path.endsWith("update_websocket.bat") ||
                path.endsWith("package.json") ||
                path.endsWith("package-lock.json") ;
    }

    public String prependStaticUrlsWithHydraPath(String html, Session session) {
        String hydraPath = session.getHydraPath();
        if(null == hydraPath) return html;

        for(Map.Entry<String, String> staticResourceEntry : staticResourcesToReplace.entrySet()) {
            html = html.replace(staticResourceEntry.getKey(), staticResourceEntry.getValue().replace(HYDRA_PATH, hydraPath));
        }

        return html;
    }

    public void testLoadStaticResource(String path) {
        Set<String> set = new HashSet<>();
        set.add(path.substring(path.indexOf("/static/") + 8));
        staticResourcesAvailable = set;
    }

    public String detectUsedResources(String html) {
        Document document = Jsoup.parse(html);
        detectUsedResourcesInElements("href", document.getElementsByAttribute("href"));
        detectUsedResourcesInElements("src", document.getElementsByAttribute("src"));
        return html;
    }

    private void detectUsedResourcesInElements(String key, Elements elements) {
        for(Element hrefAttribute : elements) {
            final String urlUsed = hrefAttribute.attr(key);
            String matchLookup = urlUsed;
            if(matchLookup.startsWith("/")) {
                matchLookup = matchLookup.substring(1);
            }
            if(staticResourcesAvailable.contains(matchLookup)) {
                staticResourcesToReplace.put(hrefAttribute.toString(), hrefAttribute.toString().replace(urlUsed, "/" + HYDRA_PATH + "/" + matchLookup));
            }
        }
    }

    public Set<String> getAllResources() {
        return staticResourcesAvailable;
    }
}
