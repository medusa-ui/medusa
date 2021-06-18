package io.getmedusa.medusa.core.annotation;

import java.util.HashMap;
import java.util.Map;

public class PageSetup {

    private final String getPath;
    private final String htmlFile;
    private final Map<String, Object> pageVariables = new HashMap<>();

    public PageSetup(String getPath, String htmlFile) {
        this.getPath = getPath;
        this.htmlFile = htmlFile;
    }

    public PageSetup(String getPath, String htmlFile, Map<String, Object> pageVariables) {
        this.getPath = getPath;
        this.htmlFile = htmlFile;
        if(null != pageVariables) this.pageVariables.putAll(pageVariables);
    }

    public String getGetPath() {
        return getPath;
    }

    public String getHtmlFile() {
        return htmlFile;
    }

    public Map<String, Object> getPageVariables() {
        return pageVariables;
    }
}
