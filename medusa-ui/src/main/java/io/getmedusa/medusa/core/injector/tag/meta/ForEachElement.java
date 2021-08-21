package io.getmedusa.medusa.core.injector.tag.meta;

import java.util.Objects;

public class ForEachElement implements Comparable<ForEachElement> {

    private static final String END_FOR = "[$end for]";

    public final ForEachElement parent;
    public final String blockHTML; //block including the foreach block and condition
    public final String innerHTML; //block within the foreach
    public final String condition; //the relevant foreach condition
    public final int depth;
    private RenderInfo childRenderInfo;

    public ForEachElement(String block, ForEachElement parent) {
        String innerBlock = block.substring(block.indexOf(']') + 1, block.length() - END_FOR.length());
        this.blockHTML = block;
        this.innerHTML = innerBlock;
        this.condition = parseCondition(block);
        this.parent = parent;
        if(parent == null) {
            this.depth = 0;
        } else {
            this.depth = parent.depth+1;
        }
    }

    public RenderInfo getChildRenderInfo() {
        return childRenderInfo;
    }

    public void setChildRenderInfo(RenderInfo childRenderInfo) {
        this.childRenderInfo = childRenderInfo;
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

    @Override
    public int compareTo(ForEachElement elem) {
        return Integer.compare(elem.depth, depth);
    }

    private String parseCondition(String block) {
        return block.substring("[$foreach".length(), block.indexOf("]")).trim().substring(1); //TODO error if condition does not start with $
    }

    public String renderWithChildRenders(RenderInfo renderInfo) {
        return renderInfo.merge(childRenderInfo).combine();
    }

    public static class RenderInfo {
        final String blockToReplace;
        public String template;
        public String divs;

        public RenderInfo(final String blockToReplace) {
            this.blockToReplace = blockToReplace;
        }

        public String combine() {
            return template + divs;
        }

        public RenderInfo merge(RenderInfo childRenderInfo) {
            if(childRenderInfo != null) {
                template = template.replace(childRenderInfo.blockToReplace, childRenderInfo.template);
                divs = divs.replace(childRenderInfo.blockToReplace, childRenderInfo.divs);
            }
            return this;
        }
    }
}
