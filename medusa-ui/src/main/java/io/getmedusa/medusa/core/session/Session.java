package io.getmedusa.medusa.core.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.getmedusa.medusa.core.annotation.UIEventPageCallWrapper;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.attributes.StandardAttributeKeys;
import io.getmedusa.medusa.core.boot.RefDetection;
import io.getmedusa.medusa.core.router.action.FileUploadMeta;
import io.getmedusa.medusa.core.router.action.SocketSink;
import io.getmedusa.medusa.core.router.request.Route;
import io.getmedusa.medusa.core.util.AttributeUtils;
import io.getmedusa.medusa.core.util.RandomUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

public class Session {

    private final String id;
    private final String password;
    private String lastUsedTemplate;
    private String lastUsedHash;
    private String lastRenderedHTML;
    private List<Attribute> lastParameters = new ArrayList<>();
    private Map<String, String> tags = new HashMap<>();
    private List<String> fragments = new ArrayList<>();
    private final String hydraPath;
    private boolean matched;
    private boolean initialRender = true;
    private Locale locale = Locale.US;

    private Map<String, FileUploadMeta> pendingFileUploads = new HashMap<>();

    private int depth;
    @JsonIgnore
    private final SocketSink sink = new SocketSink();

    public Session() {
        this.id = RandomUtils.generateId();
        this.password = RandomUtils.generatePassword(id);
        this.hydraPath = null;
    }

    public Session(String hydraPath) {
        this.id = RandomUtils.generateId();
        this.password = RandomUtils.generatePassword(id);
        this.hydraPath = hydraPath;
    }

    public Session(Route route, ServerRequest request) {
        this.id = RandomUtils.generateId();
        this.password = RandomUtils.generatePassword(id);
        this.hydraPath = findHydraPath(request.headers());

        setLastUsedTemplate(route.getTemplateHTML());
        setLastUsedHash(route.generateHash());
        getTags().put(StandardSessionTagKeys.ROUTE, request.path());
        getTags().put(StandardSessionTagKeys.CONTROLLER, route.getController().getClass().getName());
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
        /*System.out.println("---");
        System.out.println(lastRenderedHTML);
        System.out.println("---");*/
        this.lastRenderedHTML = lastRenderedHTML;
    }

    public List<Attribute> getLastParameters() {
        return lastParameters;
    }

    public void setLastParameters(List<Attribute> lastParameters) {
        this.lastParameters = lastParameters;
    }

    public Map<String, Object> toLastParameterMap() {
        return AttributeUtils.toLastParameterMap(this.lastParameters);
    }

    public boolean removeAttributeByName(String name) {
        return lastParameters.removeIf(attribute -> attribute.name().equals(name));
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

    public String getHydraPath() {
        return hydraPath;
    }

    public void setInitialRender(boolean initialRender) {
        this.initialRender = initialRender;
    }

    public boolean isInitialRender() {
        return initialRender;
    }

    public Session merge(Collection<Attribute> newAttributes) {
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

    private String findHydraPath(ServerRequest.Headers headers) {
        if(headers == null) {
            return null;
        }
        List<String> relevantHeaders = headers.header("hydra-path");
        if(relevantHeaders.isEmpty()) {
            return null;
        }
        return relevantHeaders.get(0);
    }

    public SocketSink getSink() {
        return sink;
    }

    public void setMatched() {
        this.matched = true;
    }

    public boolean isMatched() {
        return matched;
    }

    public void putTag(String tagKey, String tagValue) {
        this.getTags().put(tagKey, tagValue);
    }

    public String getTag(String tagKey) {
        return this.getTags().getOrDefault(tagKey, null);
    }

    public <T> T getAttribute(String attributeKey) {
        return (T) lastParameters.stream()
                .filter(a -> a.name().equals(attributeKey))
                .findFirst()
                .orElse(new Attribute())
                .value();
    }

    public List<Attribute> findPassThroughAttributes() {
        List<Attribute> passThrough = this.lastParameters.stream()
                .filter(p -> StandardAttributeKeys.findAllPassThroughKeys().contains(p.name()))
                .toList();
        lastParameters.removeAll(passThrough);
        return passThrough;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public String getUsername() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, FileUploadMeta> getPendingFileUploads() {
        return pendingFileUploads;
    }

    public void setPendingFileUploads(Map<String, FileUploadMeta> pendingFileUploads) {
        this.pendingFileUploads = pendingFileUploads;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Session withLocale(String localeString) {
        this.setLocale(Locale.forLanguageTag(localeString.trim()));
        return this;
    }

    public Flux<Session> setupAttributes(String ref, boolean fragmentFallback) {
        if(!fragmentFallback) {
            UIEventPageCallWrapper bean = RefDetection.INSTANCE.findBeanByRef(ref);
            if(isInitialRender()) { //TODO ? what happens 'on action'?
                //call controller startup and use for render (add to session? separate?)
                Mono<List<Attribute>> attributes = bean.setupAttributes(null, this);
                return attributes.flatMapMany(a -> Flux.just(merge(a)));
            }
        }
        return Flux.just(this);
    }

    public void addFragmentTag(String bean) {
        if(bean != null) {
            fragments.add(bean);
        }
    }

    public List<String> getFragments() {
        return fragments;
    }
}