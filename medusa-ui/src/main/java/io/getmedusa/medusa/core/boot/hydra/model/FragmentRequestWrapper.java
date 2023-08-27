package io.getmedusa.medusa.core.boot.hydra.model;

import io.getmedusa.medusa.core.boot.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//incoming request from hydra to resolve fragments
public class FragmentRequestWrapper {

    private Map<String, Object> attributes;
    private List<Fragment> requests = new ArrayList<>();

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<Fragment> getRequests() {
        return requests;
    }

    public void setRequests(List<Fragment> requests) {
        this.requests = requests;
    }

    @Override
    public String toString() {
        return "FragmentRequestWrapper{" +
                "attributes=" + attributes +
                ", requests=" + requests +
                '}';
    }
}
