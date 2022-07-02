package io.getmedusa.medusa.core.boot.hydra.model;

import io.getmedusa.medusa.core.boot.Fragment;

import java.util.List;
import java.util.Map;
//used to ask hydra for several fragments
public class FragmentHydraRequestWrapper {

    private Map<String, Object> attributes;
    private Map<String, List<Fragment>> requests;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, List<Fragment>> getRequests() {
        return requests;
    }

    public void setRequests(Map<String, List<Fragment>> requests) {
        this.requests = requests;
    }
}
