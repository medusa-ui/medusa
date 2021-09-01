package io.getmedusa.medusa.core.injector.tag.meta;

public class Div implements Comparable<Div> {

    private ParentChain chainOnThisLevel;
    //private String htmlToReplace;
    private String resolvedHTML;
    private Div parent;
    private final int depth;
    private final ForEachElement originalElement;

    //parent
    public Div(ForEachElement originalElement) {
        this.originalElement = originalElement;
        //this.htmlToReplace = originalElement.blockHTML;
        this.depth = 0;
    }

    //child
    public Div(ForEachElement originalElement, Object eachObject, Div parent) {
        this.originalElement = originalElement;
        //this.htmlToReplace = originalElement.innerHTML;
        this.resolvedHTML = "";
        this.chainOnThisLevel = new ParentChain(eachObject, parent.getChainOnThisLevel());
        this.parent = parent;
        this.depth = parent.depth + 1;
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

    public ParentChain getChainOnThisLevel() {
        return chainOnThisLevel;
    }

    public void setChainOnThisLevel(ParentChain chainOnThisLevel) {
        this.chainOnThisLevel = chainOnThisLevel;
    }

    /*public String getHtmlToReplace() {
        return htmlToReplace;
    }*/

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

    public void appendToResolvedHTML(String toAppend) {
        this.resolvedHTML = this.resolvedHTML + toAppend;
    }
}