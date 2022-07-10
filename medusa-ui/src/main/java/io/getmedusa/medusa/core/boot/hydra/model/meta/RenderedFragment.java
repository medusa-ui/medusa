package io.getmedusa.medusa.core.boot.hydra.model.meta;

public class RenderedFragment {

    private String renderedHTML;
    private String id;

    public String getRenderedHTML() {
        return renderedHTML;
    }

    public void setRenderedHTML(String renderedHTML) {
        this.renderedHTML = renderedHTML;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RenderedFragment{" +
                "renderedHTML='" + renderedHTML + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
