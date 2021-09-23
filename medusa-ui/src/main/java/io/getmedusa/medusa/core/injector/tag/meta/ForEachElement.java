package io.getmedusa.medusa.core.injector.tag.meta;

import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.util.IdentifierGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ForEachElement implements Comparable<ForEachElement>  {

    private static final String TAG_EACH = "[$each]";
    private static final String TAG_THIS_EACH = "[$this.each]";

    public final String blockHTML; //block including the foreach block and condition
    public String innerHTML; //block within the foreach
    public final String condition; //the relevant foreach condition
    public ForEachElement parent;

    private List<ForEachElement> children;
    private int depth = 0;

    //private RenderInfo childRenderInfo;

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

    public void setChildren(List<ForEachElement> children) {
        this.children = children;
    }

    public int getDepth() {
        return depth;
    }

    public RenderInfo getChildRenderInfo() {
        return null;
    }

    public void setChildRenderInfo(RenderInfo childRenderInfo) {
        /*final String replacementTag = "[$$replace-" + childRenderInfo.templateID + ']';
        this.innerHTML = this.innerHTML.replace(childRenderInfo.blockToReplace, replacementTag);
        childRenderInfo.blockToReplace = replacementTag;
        this.childRenderInfo = childRenderInfo;*/
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
        if(!block.contains("[$foreach")) return null;
        return block.substring("[$foreach".length(), block.indexOf("]")).trim().substring(1); //TODO error if condition does not start with $
    }

    public String renderWithChildRenders(RenderInfo renderInfo) {
        //return renderInfo.merge(childRenderInfo).combine();
        return null;
    }

    @Override
    public int compareTo(ForEachElement elem) {
        return Integer.compare(elem.depth, depth);
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public void addChild(ForEachElement child) {
        if(this.children == null) this.children = new ArrayList<>();
        this.children.add(child);
    }

    public boolean hasChildren() {
        return this.children != null && !this.children.isEmpty();
    }

    public ForEachElement getParent() {
        return parent;
    }

    /*
    Final method call, renders template + child divs
     */
    public String render() {
        final String templateID = IdentifierGenerator.generateTemplateID(this);
        IterationRegistry.getInstance().add(templateID, condition);
        String template = "<template m-id=\"" + templateID + "\">" + innerHTML.replace(TAG_EACH, TAG_THIS_EACH) + "</template>";
        return template + renderChildDivs();
    }

    private String renderChildDivs() {
        return "";
    }

    public void merge(ForEachElement childElementToMergeWith) {
        //TODO merge child divs
        //TODO you need to know which one belongs to which each instance ...
        System.out.println();
        //String resolvedInnerHTML = resolveInnerHTML(childElementToMergeWith.innerHTML);
    }

    public static class RenderInfo {
        final String templateID;
        String blockToReplace;
        public String template;
        public List<String> divs;

        public RenderInfo(final String blockToReplace, final String templateID) {
            this.blockToReplace = blockToReplace;
            this.templateID = templateID;
        }

        public String combine() {
            return template + renderDivs();
        }

        public RenderInfo merge(RenderInfo childRenderInfo) {
            if(childRenderInfo != null) {
                template = template.replace(childRenderInfo.blockToReplace, childRenderInfo.template).replace(TAG_EACH, TAG_THIS_EACH);
                for (int i = 0; i < divs.size(); i++) {
                    divs.set(i, divs.get(i).replace(childRenderInfo.blockToReplace, childRenderInfo.renderDivs()));
                }
            }
            return this;
        }

        public String renderDivs() {
            StringBuilder builder = new StringBuilder();
            for(String div : divs) {
                builder.append(div);
            }
            return builder.toString();
        }
    }
}
