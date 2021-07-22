package io.getmedusa.medusa.core.annotation;

import io.getmedusa.medusa.core.util.FilenameHandler;

import java.util.HashMap;
import java.util.Map;

public class PageSetup {

    private final String getPath;
    private final String htmlFile;
    private final Map<String, Object> pageVariables = new HashMap<>();

    public PageSetup(String getPath, String htmlFile) {
        this(getPath, htmlFile, new HashMap<>());
    }

    public PageSetup(String getPath, String htmlFile, Map<String, Object> pageVariables) {
        this.getPath = getPath;
        this.htmlFile = htmlFile;
        if(null != pageVariables) this.pageVariables.putAll(pageVariables);
    }

    public PageSetup with(String key, Object value) {
        pageVariables.put(key, value);
        return this;
    }

    public String getGetPath() {
        return getPath;
    }

    public String getHtmlFile() {
        return FilenameHandler.removeExtension(FilenameHandler.normalize(htmlFile));
    }

    public Map<String, Object> getPageVariables() {
        return pageVariables;
    }

    public PageSetup withPathVariable(String key, String pathVariable) {
        // how & where do we get the value from the actual call (url) ?
        pageVariables.put(key, pathVariable);
        return this;
    }
}
