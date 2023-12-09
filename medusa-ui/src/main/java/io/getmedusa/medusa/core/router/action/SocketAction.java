package io.getmedusa.medusa.core.router.action;

import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;

import java.util.Map;

//incoming browser change
public class SocketAction {

    private String fragment;
    private String action;
    private FileUploadMeta fileMeta;
    private Map<String, Object> metadata;

    public String getFragment() {
        if(JSEventAttributeProcessor.FRAGMENT.equals(fragment)) {
            return null;
        }
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

    public FileUploadMeta getFileMeta() {
        return fileMeta;
    }

    public void setFileMeta(FileUploadMeta fileMeta) {
        this.fileMeta = fileMeta;
    }
}