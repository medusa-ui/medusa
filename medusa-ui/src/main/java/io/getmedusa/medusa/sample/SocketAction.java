package io.getmedusa.medusa.sample;

import java.util.Map;

public class SocketAction {

    private String fragment;
    private String action;
    private Map<String, Object> metadata;

    public SocketAction() {}

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}