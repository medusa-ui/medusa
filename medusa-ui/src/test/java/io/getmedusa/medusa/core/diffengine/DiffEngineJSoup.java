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
import java.util.UUID;

public class DiffEngineJSoup {

    protected final DiffEngine diffEngine = new DiffEngine();

    protected String applyDiff(String oldHTML, JSReadyDiff diff, Map<JSReadyDiff, Element> elementMap) {
        var html = Jsoup.parse(oldHTML);

        if(diff.isAddition()) {
            handleAddition(diff, html);
        } else if(diff.isRemoval()) {
            handleRemoval(diff, html);
        } else if(diff.isSequenceChange()) {
            handleSequenceChange(diff, html, elementMap);
        } else if(diff.isEdit()) {
            handleEdit(diff, html);
        } else {
            throw new NotImplementedException("diff not implemented: " + diff);
        }

        return html.getElementsByTag("section").get(0).outerHtml();
    }

    private void handleEdit(JSReadyDiff diff, Document html) {
        Element element = xpath(html, diff.getXpath()).get(0);
        element.replaceWith(toElement(diff.getContent()));
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

    private void handleRemoval(JSReadyDiff diff, Document html) {
        if(diff.getXpath().endsWith("text()[1]")) {
            xpath(html, diff.getXpath().replace("/text()[1]", "")).textNodes().get(0).remove();
        } else {
            xpath(html, diff.getXpath()).remove();
        }
    }

    //TODO this one has issues
    private void handleSequenceChange(JSReadyDiff diff, Document html, Map<JSReadyDiff, Element> elementMap) {
        Element elemToMove = xpath(html, diff.getXpath()).get(0);

        if("::LAST".equals(diff.getContent())) {
            elemToMove.parent().appendChild(elementMap.get(diff));
        } else {
            final Element addBefore = xpath(html, diff.getContent()).get(0);
            addBefore.before(elementMap.get(diff).outerHtml());
        }
        html.selectXpath("//*[@uuid='"+diff.getAttribute()+"']").remove();
    }

    protected void applyAndTest(String oldHTML, String newHTML, List<JSReadyDiff> jsReadyDiffs) {
        Map<JSReadyDiff, Element> elementMap = new HashMap<>();
        var parsedHTMLForLookups = Jsoup.parse(oldHTML);

        System.out.println("Initial HTML: \n" + parsedHTMLForLookups.getElementsByTag("section").get(0).outerHtml());

        //prep sequences first
        for(JSReadyDiff diff : jsReadyDiffs) {
            if(diff.isSequenceChange()) {
                Element elem = parsedHTMLForLookups.selectXpath("/" + diff.getXpath()).get(0);
                String uuid = UUID.randomUUID().toString();
                diff.setAttribute(uuid);
                elementMap.put(diff, elem.clone());
                elem.attr("uuid", uuid);
            }
        }
        if(!elementMap.isEmpty()) {
            oldHTML = parsedHTMLForLookups.outerHtml();
        }

        for(JSReadyDiff diff : jsReadyDiffs) {
            oldHTML = applyDiff(oldHTML, diff, elementMap);

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
