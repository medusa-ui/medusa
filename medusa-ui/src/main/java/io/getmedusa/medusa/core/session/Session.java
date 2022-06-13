package io.getmedusa.medusa.core.session;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.router.request.Route;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Session {

    private String lastUsedTemplate;
    private String lastUsedHash;
    private String lastRenderedHTML;
    private List<Attribute> lastParameters = new ArrayList<>();
    private Map<String, String> tags = new HashMap<>();

    public Session() {}

    public Session(Route route, ServerRequest request) {
        setLastParameters(route.getSetupAttributes(request));
        setLastUsedTemplate(route.getTemplateHTML());
        setLastUsedHash(route.generateHash());
        getTags().put(StandardSessionTags.CURRENT_ROUTE.name(), route.getPath());
    }

    public String getLastUsedTemplate() {
        return lastUsedTemplate;
    }

    public void setLastUsedTemplate(String lastUsedTemplate) {
        this.lastUsedTemplate = lastUsedTemplate;
    }

    public String getLastUsedHash() {
        return lastUsedHash;
    }

    public void setLastUsedHash(String lastUsedHash) {
        this.lastUsedHash = lastUsedHash;
    }

    public String getLastRenderedHTML() {
        return lastRenderedHTML;
    }

    public void setLastRenderedHTML(String lastRenderedHTML) {
        this.lastRenderedHTML = lastRenderedHTML;
    }

    public List<Attribute> getLastParameters() {
        return lastParameters;
    }

    public void setLastParameters(List<Attribute> lastParameters) {
        this.lastParameters = lastParameters;
    }

    public Map<String, Object> toLastParameterMap() {
        return this.lastParameters.stream().collect(Collectors.toMap(Attribute::name, Attribute::value, (a, b) -> b));
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
