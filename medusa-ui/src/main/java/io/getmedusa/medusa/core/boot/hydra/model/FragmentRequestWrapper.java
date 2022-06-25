package io.getmedusa.medusa.core.boot.hydra.model;

import io.getmedusa.medusa.core.boot.hydra.model.meta.FragmentRequest;

import java.util.List;
import java.util.Map;

public class FragmentRequestWrapper {

    private Map<String, Object> attributes;
    private List<FragmentRequest> requests;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<FragmentRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<FragmentRequest> requests) {
        this.requests = requests;
    }
}
