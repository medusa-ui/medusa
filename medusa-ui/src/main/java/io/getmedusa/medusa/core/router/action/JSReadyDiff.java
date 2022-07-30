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
        return new JSReadyDiff(content, determinePreviousNode(xPath), DiffType.ADDITION);
    }

    public static JSReadyDiff buildNewRedirect(String url) {
        return new JSReadyDiff(url, null, DiffType.REDIRECT);
    }

    static String determinePreviousNode(String xPath) {
        final int beginIndex = xPath.lastIndexOf("[") + 1;
        final int endIndex = xPath.length() - 1;
        int index = Integer.parseInt(xPath.substring(beginIndex, endIndex));
        if(index == 1) {
            return xPath.substring(0, xPath.lastIndexOf("/") + 1) + "::first";
        } else {
            return xPath.substring(0, beginIndex) + (index-1) + "]";
        }
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
        REMOVAL,
        TAG_CHANGE,
        REDIRECT
    }

    public boolean isEdit() {
        return DiffType.EDIT.equals(this.type);
    }

    @Override
    public String toString() {
        return "JSReadyDiff{" +
                "content='" + content + '\'' +
                ", xpath='" + xpath + '\'' +
                ", type=" + type +
                '}';
    }
}
