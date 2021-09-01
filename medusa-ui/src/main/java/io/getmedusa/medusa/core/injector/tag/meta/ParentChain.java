package io.getmedusa.medusa.core.injector.tag.meta;

public class ParentChain {
    private Object eachObject;
    private ParentChain parent;

    public ParentChain(Object eachObject, ParentChain chain) {
        this.eachObject = eachObject;
        this.parent = chain;
    }

    public ParentChain(Object eachObject) {
        this.eachObject = eachObject;
    }

    public Object getEachObject() {
        return eachObject;
    }

    public void setEachObject(Object eachObject) {
        this.eachObject = eachObject;
    }

    public ParentChain getParent() {
        return parent;
    }

    public void setParent(ParentChain parent) {
        this.parent = parent;
    }
}
