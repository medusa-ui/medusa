package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.session.Session;
import io.getmedusa.medusa.core.util.LoaderUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum StaticResourcesDetection {

    INSTANCE;

    private static final String HYDRA_PATH = "{hydrapath}";
    private static final String STATIC = "static";

    public static String LOADER_GLOBAL = null;
    public static String LOADER_BUTTON = null;

    private Set<String> staticResourcesAvailable = new HashSet<>();
    private final Map<String, String> staticResourcesToReplace = buildWithDefaults();

    private Map<String, String> buildWithDefaults() {
        final Map<String, String> map = new HashMap<>();
        map.put("script src=\"/websocket.js\"", "script src=\"/"+ HYDRA_PATH +"/websocket.js\"");
        return map;
    }

    public void detectAvailableStaticResources() {
        try {
            staticResourcesAvailable = determineListOfStaticResources();
        } catch (IOException e) {
            throw new IllegalStateException("Could not detect static resources", e);
        }
    }

    private Set<String> determineListOfStaticResources() throws IOException {
        Set<String> set = new HashSet<>();

        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
        Resource[] resources = resourcePatternResolver.getResources("**");

        for (Resource resource : resources) {
            final String path = resource.getURI().toString();

            if(LoaderUtils.isPathGlobalLoader(path)) {
                LOADER_GLOBAL = LoaderUtils.loadGlobalLoader(path);
            } else if(LoaderUtils.isPathButtonLoader(path)) {
                LOADER_BUTTON = LoaderUtils.loadPathButtonLoader(path);
            } else if (path != null && path.contains(STATIC) && !isPartOfStandardMedusaSetup(path)) {
                final String finalPath = path.substring(path.indexOf(STATIC) + 7);
                if(!finalPath.isBlank()) {
                    set.add(finalPath);
                }
            }
        }
        set.add("websocket.js");
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
        if(null == hydraPath) {
            return html;
        }

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
                String reference = hrefAttribute.toString();
                reference = reference.substring(reference.indexOf("<") + 1, reference.indexOf(">"));
                staticResourcesToReplace.put(reference, reference.replace(urlUsed, "/" + HYDRA_PATH + "/" + matchLookup));
            }
        }
    }

    public Set<String> getAllResources() {
        return staticResourcesAvailable;
    }
}
