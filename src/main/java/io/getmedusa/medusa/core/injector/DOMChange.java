package io.getmedusa.medusa.core.injector;

public class DOMChange {

    private String f;
    private String v;

    public DOMChange(String field, String value) {
        this.f = field;
        this.v = value;
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
}
