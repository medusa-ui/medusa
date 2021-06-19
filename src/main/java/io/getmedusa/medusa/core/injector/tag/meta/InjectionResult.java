package io.getmedusa.medusa.core.injector.tag.meta;

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

    public InjectionResult removeFromTitle(String regex) {
        String[] splitHTML = getHtml().split("</title>");

        if(splitHTML.length == 2) {
            setHtml(splitHTML[0].replaceFirst(regex, "") + "</title>" + splitHTML[1]);
        }

        return this;
    }

    public String replaceFinal(String key, String value) {
        setHtml(getHtml().replaceFirst(key, value));
        //TODO add scripts
        return this.getHtml();
    }

    public InjectionResult replaceAll(String key, String value) {
        setHtml(getHtml().replaceAll(key, value));
        return this;
    }
}
