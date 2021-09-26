package io.getmedusa.medusa.core.injector.tag.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Div implements Comparable<Div> {

    private ParentChain chainOnThisLevel;
    private String resolvedHTML = "";
    private Div parent;
    private final List<Div> children = new ArrayList<>();

    private final int depth;
    private final ForEachElement originalElement;

    //parent
    public Div(ForEachElement originalElement) {
        this.originalElement = originalElement;
        this.depth = 0;
    }

    //child
    public Div(ForEachElement originalElement, Object eachObject, Div parent) {
        this.originalElement = originalElement;
        this.chainOnThisLevel = new ParentChain(eachObject, (parent == null) ? null : parent.getChainOnThisLevel());
        this.parent = parent;
        this.depth = (parent == null) ? 0 : parent.depth + 1;
    }

    public List<Div> getChildren() {
        return children;
    }

    public ForEachElement getElement() {
        return originalElement;
    }

    public String getResolvedHTML() {
        return resolvedHTML;
    }

    @Override
    public int compareTo(Div elem) {
        return Integer.compare(elem.depth, depth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Div)) return false;
        Div div = (Div) o;
        return depth == div.depth && Objects.equals(getParent(), div.getParent()) && originalElement.equals(div.originalElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParent(), depth, originalElement);
    }

    public ParentChain getChainOnThisLevel() {
        return chainOnThisLevel;
    }

    public void setResolvedHTML(String resolvedHTML) {
        this.resolvedHTML = resolvedHTML;
    }

    public Div getParent() {
        return parent;
    }

    public void setParent(Div parent) {
        this.parent = parent;
    }

    public boolean isRoot() {
        return this.depth == 0;
    }

}