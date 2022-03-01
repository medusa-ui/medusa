package io.getmedusa.medusa.core.injector.tag.meta;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class InjectionResult {

    private Document document;
    private List<String> scripts = new ArrayList<>();

    public InjectionResult(String html) {
        this.document = Jsoup.parse(html,Parser.xmlParser());
    }

    public InjectionResult(Document html) {
        this.document = html;
    }

    public void addScript(String script) {
        this.scripts.add(script);
    }

    public String getHTML() {
        return document.html();
    }

    public Document getDocument() { return document; }

    public void setDocument(Document document) {
        this.document = document;
    }

    public List<String> getScripts() {
        return scripts;
    }

    public void setScripts(List<String> scripts) {
        this.scripts = scripts;
    }

    @Override
    public String toString() {
        return "InjectionResult{" +
                "document=" + document +
                '}';
    }

    /*
    public InjectionResult removeFromTitle(String regex) {
        String[] splitHTML = getHtml().split("</title>");

        if(splitHTML.length == 2) {
            setHtml(splitHTML[0].replaceFirst(regex, "") + "</title>" + splitHTML[1]);
        }

        return this;
    }

    public InjectionResult replaceAll(String key, String value) {
        setHtml(getHtml().replaceAll(key, value));
        return this;
    }*/
}
