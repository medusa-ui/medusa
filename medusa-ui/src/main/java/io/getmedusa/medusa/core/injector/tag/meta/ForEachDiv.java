package io.getmedusa.medusa.core.injector.tag.meta;

public class ForEachDiv {

    private final int index;
    private final String templateID;
    private final String innerBlock;

    public ForEachDiv(int index, String templateID, String innerBlock) {
        this.index = index;
        this.templateID = templateID;
        this.innerBlock = innerBlock;
    }

    @Override
    public String toString() {
        return "<div index=\"" + index + "\" template-id=\"" + templateID + "\">\n" + innerBlock + "\n</div>\n";
    }
}
