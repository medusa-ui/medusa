package io.getmedusa.medusa.core.injector.tag.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ForEachElement implements Comparable<ForEachElement>  {

    protected static final String FOREACH = "[$foreach";
    protected static final int FOREACH_LENGTH = FOREACH.length();

    public final String blockHTML; //block including the foreach block and condition
    public String innerHTML; //block within the foreach
    public final String condition; //the relevant foreach condition
    public ForEachElement parent;

    private List<ForEachElement> children = new ArrayList<>();
    private int depth = 0;

    public ForEachElement(String block, String innerBlock) {
        this.blockHTML = block;
        this.innerHTML = innerBlock;
        this.condition = parseCondition(block);
    }

    public void setParent(ForEachElement parent) {
        this.parent = parent;
        if(parent == null) {
            this.depth = 0;
        } else {
            this.depth = parent.depth+1;
        }
    }

    public List<ForEachElement> getChildren() {
        return children;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForEachElement)) return false;
        ForEachElement element = (ForEachElement) o;
        return depth == element.depth && blockHTML.equals(element.blockHTML);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockHTML, depth);
    }

    private String parseCondition(String block) {
        if(!block.contains(FOREACH)) return null;
        return block.substring(block.indexOf(FOREACH) + FOREACH_LENGTH, block.indexOf("]", block.indexOf(FOREACH))).trim().substring(1);
    }

    @Override
    public int compareTo(ForEachElement elem) {
        return Integer.compare(elem.depth, depth);
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public void addChild(ForEachElement child) {
        this.children.add(child);
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public ForEachElement getParent() {
        return parent;
    }

}
