package io.getmedusa.medusa.core.injector;

import java.util.ArrayList;
import java.util.List;

public class DOMChanges {

    private final List<DOMChange> domChanges = new ArrayList<>();

    public DOMChanges(String field, Object value) {
        domChanges.add(new DOMChange(field, value));
    }

    public DOMChanges(String field, Object value, DOMChange.DOMChangeType type) {
        domChanges.add(new DOMChange(field, value, type));
    }

    public DOMChanges and(String field, Object value) {
        domChanges.add(new DOMChange(field, value));
        return this;
    }

    public DOMChanges and(String field, Object value, DOMChange.DOMChangeType type) {
        domChanges.add(new DOMChange(field, value, type));
        return this;
    }

    public static DOMChanges of(String field, Object value) {
        return new DOMChanges(field, value);
    }

    public static DOMChanges of(String field, Object value, DOMChange.DOMChangeType type) {
        return new DOMChanges(field, value, type);
    }

    public List<DOMChange> build() {
        return domChanges;
    }

    public static class DOMChange {

        private String f; //field
        private Object v; //value (value or relevant id)
        private Integer t; //type
        private String c; //condition

        public DOMChange(String field, Object value) {
            this.f = field;
            this.v = value;
        }

        public DOMChange(String field, Object value, DOMChange.DOMChangeType type) {
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

        public Integer getT() {
            return t;
        }

        public void setT(Integer t) {
            this.t = t;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public enum DOMChangeType {
            TITLE,
            CONDITION,
            ITERATION,
            CONDITIONAL_CLASS,
            M_ATTR;
        }
    }
}
