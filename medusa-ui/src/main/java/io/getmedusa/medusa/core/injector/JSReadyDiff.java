package io.getmedusa.medusa.core.injector;

public class JSReadyDiff {

    private String content;
    private String xpath;
    private DiffType type;

    public JSReadyDiff(DiffType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public DiffType getType() {
        return type;
    }

    public void setType(DiffType type) {
        this.type = type;
    }
}
