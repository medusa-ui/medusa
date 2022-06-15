package io.getmedusa.medusa.core.session;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.util.RandomUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Session {

    private final String id;
    private String lastUsedTemplate;
    private String lastUsedHash;
    private String lastRenderedHTML;
    private List<Attribute> lastParameters = new ArrayList<>();
    private Map<String, String> tags = new HashMap<>();

    public Session() {
        this.id = RandomUtils.generateId();
    }

    public Session(Route route, ServerRequest request) {
        this.id = RandomUtils.generateId();
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

    public String getId() {
        return id;
    }

    public Session merge(List<Attribute> newAttributes) {
        final Map<String, Object> map = toLastParameterMap();
        for(Attribute attribute : newAttributes) {
            map.put(attribute.name(), attribute.value());
        }
        this.lastParameters = new ArrayList<>();
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            this.lastParameters.add(new Attribute(entry.getKey(), entry.getValue()));
        }

        return this;
    }
}
