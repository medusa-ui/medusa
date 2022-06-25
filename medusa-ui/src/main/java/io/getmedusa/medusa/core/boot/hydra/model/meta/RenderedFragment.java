package io.getmedusa.medusa.core.boot.hydra.model.meta;

public class RenderedFragment {

    private String renderedHTML;
    private String ref;
    private String service;

    public RenderedFragment(FragmentRequest request) {
        this.ref = request.getRef();
        this.service = request.getService();
    }

    public String getRenderedHTML() {
        return renderedHTML;
    }

    public void setRenderedHTML(String renderedHTML) {
        this.renderedHTML = renderedHTML;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "RenderedFragment{" +
                "renderedHTML='" + renderedHTML + '\'' +
                ", ref='" + ref + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}
