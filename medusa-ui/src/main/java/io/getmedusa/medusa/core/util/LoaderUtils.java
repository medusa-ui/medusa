package io.getmedusa.medusa.core.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class LoaderUtils {

    private LoaderUtils() {}

    public static boolean isPathGlobalLoader(String path) {
        return path != null && path.endsWith("/fragments/_global_loader.html");
    }

    public static boolean isPathButtonLoader(String path) {
        return path != null && path.endsWith("/fragments/_button_loader.html");
    }


    public static String loadGlobalLoader(String path) {
        final String html = FileUtils.load(parseLoaderPath(path));
        return parseGlobalLoader(html);
    }

    protected static String parseGlobalLoader(String html) {
        final Document doc = Jsoup.parse(html);
        final Element fullLoader = doc.getElementById("m-full-loader");
        if(fullLoader == null) {
            throw new IllegalArgumentException("When defining a custom full page loader, it should have a wrapper with id 'm-full-loader'.");
        }
        fullLoader.attr("style", "display: none;");
        return doc.body().html();
    }

    public static String loadPathButtonLoader(String path) {
        final String html = FileUtils.load(parseLoaderPath(path));
        return parseButtonLoader(html);
    }

    protected static String parseButtonLoader(String html) {
        final Document doc = Jsoup.parse(html);
        final Elements buttonLoaders = doc.getElementsByClass("m-button-loader");
        if(buttonLoaders.size() != 1) {
            throw new IllegalArgumentException("When defining a custom button loader, it should be wrapped in a single element with class 'm-button-loader'.");
        }
        final Element buttonLoader = buttonLoaders.first();
        if(buttonLoader == null || !"m-button-loader".equals(buttonLoader.className())) {
            throw new IllegalArgumentException("When defining a custom button loader, it should be wrapped in an element with class 'm-button-loader'.");
        }

        Element template = new Element("template");
        template.id("m-template-button-load");
        doc.body().appendChild(template);
        template.appendChild(buttonLoader);

        return doc.body().html();
    }

    private static String parseLoaderPath(final String rawPath) {
        final String path = rawPath.substring(rawPath.indexOf("/classes") + 8);
        return path.substring(path.indexOf("/"));
    }
}
