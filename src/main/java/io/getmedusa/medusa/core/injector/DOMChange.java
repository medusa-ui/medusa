package io.getmedusa.medusa.core.injector;

public class DOMChange {

    private String f; //field
    private Object v; //value (value or relevant id)
    private Integer t; //type
    private String c; //condition

    public DOMChange(String field, Object value) {
        this.f = field;
        this.v = value;
    }

    public DOMChange(String field, Object value, DOMChangeType type) {
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

    public Object getV() {
        return v;
    }

    public void setV(Object v) {
        this.v = v;
    }

    public Integer getT() { return t; }

    public void setT(Integer t) { this.t = t; }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public enum DOMChangeType {
        TITLE,
        CONDITION,
        PAGE_CHANGE;
    }
}
