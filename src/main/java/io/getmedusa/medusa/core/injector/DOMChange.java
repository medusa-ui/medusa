package io.getmedusa.medusa.core.injector;

public class DOMChange {

    private String f;
    private String v;
    private Integer t;

    public DOMChange(String field, String value) {
        this.f = field;
        this.v = value;
    }

    public DOMChange(String field, String value, DOMChangeType type) {
        this.f = field;
        this.v = value;
        this.t = type.ordinal();
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public Integer getT() { return t; }

    public void setT(Integer t) { this.t = t; }

    public enum DOMChangeType {
        TITLE;
    }
}
