package io.getmedusa.medusa.core.injector.tag.meta;

import java.util.Objects;

public class ForEachElement implements Comparable<ForEachElement>  {

    private static final String TAG_EACH = "[$each]";
    private static final String TAG_THIS_EACH = "[$this.each]";

    public final String blockHTML; //block including the foreach block and condition
    public String innerHTML; //block within the foreach
    public final String condition; //the relevant foreach condition

    private ForEachElement parent;
    private int depth = 0;

    private RenderInfo childRenderInfo;

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

    public ForEachElement getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    public RenderInfo getChildRenderInfo() {
        return childRenderInfo;
    }

    public void setChildRenderInfo(RenderInfo childRenderInfo) {
        final String replacementTag = "[$$replace-" + childRenderInfo.templateID + ']';
        this.innerHTML = this.innerHTML.replace(childRenderInfo.blockToReplace, replacementTag);
        childRenderInfo.blockToReplace = replacementTag;
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

    private String parseCondition(String block) {
        return block.substring("[$foreach".length(), block.indexOf("]")).trim().substring(1); //TODO error if condition does not start with $
    }

    public String renderWithChildRenders(RenderInfo renderInfo) {
        return renderInfo.merge(childRenderInfo).combine();
    }

    @Override
    public int compareTo(ForEachElement elem) {
        return Integer.compare(elem.depth, depth);
    }

    public static class RenderInfo {
        final String templateID;
        String blockToReplace;
        public String template;
        public String divs;

        public RenderInfo(final String blockToReplace, final String templateID) {
            this.blockToReplace = blockToReplace;
            this.templateID = templateID;
        }

        public String combine() {
            return template + divs;
        }

        public RenderInfo merge(RenderInfo childRenderInfo) {
            if(childRenderInfo != null) {
                template = template.replace(childRenderInfo.blockToReplace, childRenderInfo.template).replace(TAG_EACH, TAG_THIS_EACH);
                divs = divs.replace(childRenderInfo.blockToReplace, childRenderInfo.divs);
            }
            return this;
        }
    }
}
