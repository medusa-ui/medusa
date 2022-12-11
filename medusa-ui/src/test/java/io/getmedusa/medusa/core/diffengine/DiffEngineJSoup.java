package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.apache.commons.lang3.NotImplementedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiffEngineJSoup {

    protected final DiffEngine diffEngine = new DiffEngine();
    protected final CustomDiffEngine engine = new CustomDiffEngine();

    protected String applyDiff(Document html, JSReadyDiff diff, Map<JSReadyDiff, Element> elementMap) {
        if(diff.isAddition()) {
            handleAddition(diff, html);
        } else if(diff.isRemoval()) {
            handleRemoval(diff, html, elementMap);
        } else if(diff.isSequenceChange()) {
            //handleSequenceChange(diff, html, elementMap);
        } else if(diff.isAttrChange()) {
            handleAttrChange(diff, html);
        } else if(diff.isEdit()) {
            elementMap.get(diff).replaceWith(Jsoup.parse(diff.getContent()).body().child(0));
        } else {
            throw new NotImplementedException("diff not implemented: " + diff);
        }

        return html.getElementsByTag("section").get(0).outerHtml();
    }

    private void handleAttrChange(JSReadyDiff diff, Document html) {
        final Element foundElement = xpath(html, diff.getXpath()).get(0);
        if(diff.getContent().isBlank()) {
            foundElement.removeAttr(diff.getAttribute());
        } else {
            foundElement.attr(diff.getAttribute(), diff.getContent());
        }
    }

    //I believe the XMLUnit expects things to be added at the bottom
    //Code in comments is to actually add it as 'first'
    private void handleAddition(JSReadyDiff diff, Document html) {
        if(diff.getXpath().endsWith("/::first")) {
            final String parentXPath = diff.getXpath().replace("/::first", "");

            final Element foundElement = xpath(html, parentXPath).get(0);
            //if(foundElement.children().isEmpty()) {
                foundElement.append(diff.getContent());
            //} else {
                //foundElement.children().get(0).before(diff.getContent());
            //}
        } else {
            xpath(html, diff.getXpath()).after(diff.getContent());
        }
    }

    private void handleRemoval(JSReadyDiff diff, Document html, Map<JSReadyDiff, Element> elementMap) {
        if(diff.getXpath().endsWith("text()[1]")) {
            xpath(html, diff.getXpath().replace("/text()[1]", "")).textNodes().get(0).remove();
        } else {
            elementMap.get(diff).remove();
        }
    }

    protected void applyAndTest(String oldHTML, String newHTML, List<JSReadyDiff> jsReadyDiffs) {
        Map<JSReadyDiff, Element> elementMap = new HashMap<>();
        var parsedHTMLForLookups = Jsoup.parse(oldHTML);

        System.out.println("Initial HTML: \n" + parsedHTMLForLookups.getElementsByTag("section").get(0).outerHtml());

        for(JSReadyDiff diff : jsReadyDiffs) {
            if(diff.isRemoval() || diff.isEdit()) {
                Element elem = parsedHTMLForLookups.selectXpath("/" + diff.getXpath()).get(0);
                elementMap.put(diff, elem);
            }
        }
        if(!elementMap.isEmpty()) {
            oldHTML = parsedHTMLForLookups.outerHtml();
        }

        for(JSReadyDiff diff : jsReadyDiffs) {
            oldHTML = applyDiff(parsedHTMLForLookups, diff, elementMap);

            System.out.println("---");
            System.out.println("Applied: " + diff);
            System.out.println("New HTML:  \n" + noUUIDs(oldHTML));
            System.out.println("---");
        }

        Assertions.assertEquals(pretty(newHTML), pretty(oldHTML));
    }

    private String noUUIDs(String oldHTML) {
        var parsed = Jsoup.parse(oldHTML);
        for(Element e : parsed.getElementsByAttribute("uuid")) {
            e.removeAttr("uuid");
        }
        return clean(parsed);
    }

    protected String pretty(String html) {
        Document.OutputSettings outputSettings = new Document.OutputSettings();
        outputSettings.prettyPrint(true);
        var parsed = Jsoup.parse(html);
        return clean(parsed);
    }

    private String clean(Document html) {
        return html.getElementsByTag("section").get(0).outerHtml()
                .replace("\n", "")
                .replace(">  <","><")
                .replace("> <","><")
                .replace("><", ">\n<")
                .trim();
    }

    protected Element toElement(String content) {
        var html = Jsoup.parse(content);
        return html.getElementsByTag("body").get(0).child(0);
    }

    protected Elements xpath(Document html, String xpath) {
        return html.selectXpath("/" + xpath);
    }

}
