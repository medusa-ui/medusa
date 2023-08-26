package io.getmedusa.medusa.core.boot;

import java.util.ArrayList;
import java.util.List;

public class Fragment {

    private String id;
    private String service;
    private String ref;

    private List<String> imports = new ArrayList<>();
    private List<String> exports = new ArrayList<>();

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

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public List<String> getExports() {
        return exports;
    }

    public void setExports(List<String> exports) {
        this.exports = exports;
    }

    public Fragment clone() {
        Fragment f = new Fragment();
        f.id = this.id;
        f.service = this.service;
        f.ref = this.ref;
        f.fallback = this.fallback;
        return f;
    }

    @Override
    public String toString() {
        return "Fragment{" +
                "id='" + id + '\'' +
                ", service='" + service + '\'' +
                ", ref='" + ref + '\'' +
                ", fallback='" + fallback + '\'' +
                '}';
    }
}
