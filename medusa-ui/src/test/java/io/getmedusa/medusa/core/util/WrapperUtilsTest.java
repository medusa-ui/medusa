package io.getmedusa.medusa.core.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WrapperUtilsTest {

    private static final String HTML = "<p>A</p>";
    private static final String HTML_MULTIPLE_ELEMENTS = "<p>A</p><p>B</p>";
    private static final String HTML_W_WRAPPER_TO_REPLACE = "<replace-me><p>A</p></replace-me>";

    @Test
    void testSimpleWrapper() {
        Document document = Jsoup.parse(HTML);
        Element pTag = document.getElementsByTag("p").first();
        WrapperUtils.wrap(pTag, "sample-wrapper");
        System.out.println(document.html());
        Assertions.assertEquals("""
                <html>
                 <head></head>
                 <body>
                  <div class="sample-wrapper">
                   <p>A</p>
                  </div>
                 </body>
                </html>""", document.html());
    }

    @Test
    void testReplaceWrapper() {
        Document document = Jsoup.parse(HTML_W_WRAPPER_TO_REPLACE);
        Element replaceMeTag = document.getElementsByTag("replace-me").first();
        WrapperUtils.wrapAndReplace(replaceMeTag, "sample-wrapper");
        System.out.println(document.html());
        Assertions.assertEquals("""
                <html>
                 <head></head>
                 <body>
                  <div class="sample-wrapper">
                   <p>A</p>
                  </div>
                 </body>
                </html>""", document.html());
    }

    @Test
    void testWrapMultipleElements() {
        Document document = Jsoup.parse(HTML_MULTIPLE_ELEMENTS);
        Elements pTags = document.getElementsByTag("p");
        WrapperUtils.wrap(pTags, "sample-wrapper");
        System.out.println(document.html());
        Assertions.assertEquals("""
                <html>
                 <head></head>
                 <body>
                  <div class="sample-wrapper">
                   <p>A</p>
                   <p>B</p>
                  </div>
                 </body>
                </html>""", document.html());
    }

    @Test
    void testWrapMultipleElements_Single() {
        Document document = Jsoup.parse(HTML);
        Elements pTags = document.getElementsByTag("p");
        WrapperUtils.wrap(pTags, "sample-wrapper");
        System.out.println(document.html());
        Assertions.assertEquals("""
                <html>
                 <head></head>
                 <body>
                  <div class="sample-wrapper">
                   <p>A</p>
                  </div>
                 </body>
                </html>""", document.html());
    }

}
