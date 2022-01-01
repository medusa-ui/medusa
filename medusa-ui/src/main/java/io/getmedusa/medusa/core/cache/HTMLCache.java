package io.getmedusa.medusa.core.cache;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This is a registry of all raw, unparsed HTML files. <br/>
 * Rather than read them in ad hoc, we read all the HTML files in at startup time (specifically, as part of the HTMLRouter bean startup). <br/>
 * </p>
 * <p>Comments are completely stripped from the files to allow for easier parsing.</p>
 */
public class HTMLCache {

    private static final HTMLCache INSTANCE = new HTMLCache();

    public static HTMLCache getInstance() {
        return INSTANCE;
    }

    //key / html value as JSoup Document
    private static final Map<String, Document> CACHE = new HashMap<>();

    public Document getHTMLOrAdd(String filename, String html) {
        return CACHE.computeIfAbsent(filename, k -> removeAllCommentsFromHTML(html));
    }

    private Document removeAllCommentsFromHTML(String html) {
        return Jsoup.parse(html.replaceAll("(?s)<!--.*?-->", ""));
    }

    @Deprecated
    public String getHTML(String filename) {
        return CACHE.get(filename).html();
    }

    public Document getDocument(String fileName) {
        return CACHE.get(fileName).clone();
    }
}
