package io.getmedusa.medusa.core.boot;

public class Fragment {

    private String id;
    private String service;
    private String ref;
    private String fallback;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }
}
