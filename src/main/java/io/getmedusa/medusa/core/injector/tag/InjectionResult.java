package io.getmedusa.medusa.core.injector.tag;

import java.util.ArrayList;
import java.util.List;

public class InjectionResult {

    private String html;
    private List<String> scripts = new ArrayList<>();

    public InjectionResult(String html) {
        this.html = html;
    }

    public void addScript(String script) {
        this.scripts.add(script);
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<String> getScripts() {
        return scripts;
    }

    public void setScripts(List<String> scripts) {
        this.scripts = scripts;
    }

    public InjectionResult replace(String key, String value) {
        setHtml(getHtml().replace(key, value));
        return this;
    }

    public String replaceFinal(String key, String value) {
        setHtml(getHtml().replaceFirst(key, value));
        //TODO add scripts
        return this.getHtml();
    }
}
