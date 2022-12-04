package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.apache.commons.lang3.NotImplementedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DiffEngineJSoup {

    protected final DiffEngine diffEngine = new DiffEngine();

    protected String applyDiff(String oldHTML, JSReadyDiff diff) {
        var html = Jsoup.parse(oldHTML);

        if(diff.isAddition()) {
            if(diff.getXpath().endsWith("/::first")) {
                final String parentXPath = diff.getXpath().replace("/::first", "");
                xpath(html, parentXPath).append(diff.getContent());
            } else {
                xpath(html, diff.getXpath()).after(diff.getContent());
            }
        } else if(diff.isRemoval()) {
            xpath(html, diff.getXpath()).remove();
        } else if(diff.isSequenceChange()) {
            var indexToMove = Integer.parseInt(diff.getContent()) - 1;
            var indexToMoveTo = Integer.parseInt(diff.getAttribute()) - 1;

            Element elements = xpath(html, diff.getXpath()).get(0);
            var elemToMove = elements.child(indexToMove);
            var elemInCurrentPosition = elements.child(indexToMoveTo);

            elemToMove.before(elemInCurrentPosition);
        } else {
            throw new NotImplementedException("diff not implemented: " + diff);
        }

        return html.getElementsByTag("section").get(0).outerHtml();
    }

    protected String pretty(String html) {
        var parsed = Jsoup.parse(html);
        return parsed.getElementsByTag("section").get(0).outerHtml();
    }

    protected Element toElement(String content) {
        var html = Jsoup.parse(content);
        return html.getElementsByTag("body").get(0).child(0);
    }

    protected Elements xpath(Document html, String xpath) {
        return html.selectXpath("/" + xpath);
    }

}
