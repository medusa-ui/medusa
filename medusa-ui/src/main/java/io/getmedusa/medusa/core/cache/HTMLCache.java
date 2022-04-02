package io.getmedusa.medusa.core.cache;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * This is a registry of all raw, unparsed HTML files. <br/>
 * Rather than read them in ad hoc, we read all the HTML files in at startup time (specifically, as part of the HTMLRouter bean startup). <br/>
 * </p>
 * <p>Comments are completely stripped from the files to allow for easier parsing.</p>
 */
public class HTMLCache {

    public static final String CDATA_START = "/* <!CDATA[[ */";
    public static final String CDATA_END = "/* ]]> */";
    private static Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.DOTALL | Pattern.MULTILINE);

    private static final HTMLCache INSTANCE = new HTMLCache();

    public static HTMLCache getInstance() {
        return INSTANCE;
    }

    //key / html value as JSoup Document
    private static final Map<String, Document> CACHE = new HashMap<>();

    public Document getHTMLOrAdd(String filename, String html) {
        return CACHE.computeIfAbsent(filename, k -> removeAllCommentsFromHTML(scriptTagsAsPlainText(html)));
    }

    private String scriptTagsAsPlainText(String html) {
        Matcher matcher = scriptPattern.matcher(html);
        while(matcher.find()) {
            String script = matcher.group(1);
            html = html.replace(script, CDATA_START + script + CDATA_END);
        }
        return html;
    }

    private Document removeAllCommentsFromHTML(String html) {
        return Jsoup.parse(html.replaceAll("(?s)<!--.*?-->", ""), Parser.xmlParser());
    }

    @Deprecated
    public String getHTML(String filename) {
        return CACHE.get(filename).html();
    }

    public Document getDocument(String fileName) {
        return CACHE.get(fileName).clone();
    }
}
