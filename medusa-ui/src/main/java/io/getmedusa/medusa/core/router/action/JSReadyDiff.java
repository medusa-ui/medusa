package io.getmedusa.medusa.core.router.action;

//outgoing diff
public class JSReadyDiff {

    private String content;
    private String xpath;
    private DiffType type;

    public JSReadyDiff() {}

    public JSReadyDiff(String content, String xpath, DiffType type) {
        this.content = content;
        this.xpath = xpath;
        this.type = type;
    }

    public static JSReadyDiff buildNewRemoval(String xPath) {
        return new JSReadyDiff(null, xPath, DiffType.REMOVAL);
    }

    public static JSReadyDiff buildNewEdit(String xPath, String content) {
        return new JSReadyDiff(content, xPath, DiffType.EDIT);
    }

    public static JSReadyDiff buildNewAddition(String xPath, String content) {
        return new JSReadyDiff(content, xPath, DiffType.ADDITION);
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

    enum DiffType {
        ADDITION,
        EDIT,
        REMOVAL
    }

}