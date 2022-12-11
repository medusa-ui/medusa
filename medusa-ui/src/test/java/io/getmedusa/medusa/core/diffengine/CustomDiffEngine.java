package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.joox.JOOX;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CustomDiffEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomDiffEngine.class);

    public Set<JSReadyDiff> calculate(String oldHTML, String newHTML) {

        var oldDoc = JOOX.$(oldHTML);
        var newDoc = JOOX.$(newHTML);

        final Set<JSReadyDiff> diffs = new LinkedHashSet<>();

        //TODO top layer - unlikely diff
        /*final List<JSReadyDiff> topLayerDiffs = doesDifferenceExistOnThisLayer(oldDoc, newDoc, CheckDirection.OLD_VS_NEW);
        if(!topLayerDiffs.isEmpty()) {
            LOGGER.debug("Difference found on top layer");
        } else {
            LOGGER.debug("No difference found on top layer");
        }*/

        goThroughChildren(diffs, oldDoc, newDoc, CheckDirection.OLD_VS_NEW);
        goThroughChildren(diffs, newDoc, oldDoc, CheckDirection.NEW_VS_OLD);

        return diffs;
    }

    private void goThroughChildren(Set<JSReadyDiff> diffs, Match oldDoc, Match newDoc, CheckDirection direction) {
        //old vs new
        //for(var elem : oldDoc.children()) {
        for (int i = 0; i < oldDoc.children().size(); i++) {
            var elem = oldDoc.children().get(i);
            final Match elemMatch = JOOX.$(elem);

            var newElem = newDoc.children().get(i);
            final Match newElemMatch = JOOX.$(newElem);

            List<JSReadyDiff> diffsOnThisLayer = doesDifferenceExistOnThisLayer(elemMatch, newElemMatch, direction);
            if(!diffsOnThisLayer.isEmpty()) {
                diffs.addAll(diffsOnThisLayer);
            }

            //on addition, you don't need to look through children - you're adding them already!
            if(!additionNewVsOld(diffsOnThisLayer, direction)) {
                goThroughChildren(diffs, elemMatch, newDoc, direction);
            }
        }
    }

    private List<JSReadyDiff> doesDifferenceExistOnThisLayer(Match elemMatch, Match newElemMatch, CheckDirection direction) {
        final List<JSReadyDiff> diffsOnThisLayer = new LinkedList<>();

        final String xpath = elemMatch.xpath();
        //Match newElemMatch = newDoc.xpath(xpath);

        LOGGER.debug("Checking for difference on: " + xpath + " " + direction);

        //same base element?
        final Element oldElement = elemMatch.get(0);
        final Element newElement = newElemMatch.get(0);

        if(newElement == null && oldElement != null) {
            if(direction == CheckDirection.OLD_VS_NEW) {
                diffsOnThisLayer.add(JSReadyDiff.buildNewRemoval(xpath));
            } else {
                String prevXPath = elemMatch.prev().xpath();
                if(prevXPath == null) {
                    prevXPath = elemMatch.parent().xpath() + "/::first";
                }
                diffsOnThisLayer.add(JSReadyDiff.buildNewAddition(prevXPath, elemMatch.toString()));
            }
            return diffsOnThisLayer; //quick exit, because you can't compare nulls further down the line
        }

        if(baseElementDiff(oldElement, newElement) || textDifferenceDiff(oldElement, newElement)) { //TODO different tag, is that correct?
            //new vs old doesn't matter, because we only apply edits in 1 direction and there would always be 2 diffs
            if(direction == CheckDirection.OLD_VS_NEW) {
                diffsOnThisLayer.add(JSReadyDiff.buildNewEdit(xpath, newElemMatch.toString()));
            }
        }

        //difference in attributes?
        for (int i = 0; i < oldElement.getAttributes().getLength(); i++) {
            final Node oldAttribute = oldElement.getAttributes().item(i);
            final String oldAttributeValue = oldAttribute.getNodeValue();
            final String attributeName = oldAttribute.getNodeName();
            final String newValue = newElement.getAttribute(attributeName);

            if(!oldAttributeValue.equals(newValue)) {
                diffsOnThisLayer.add(JSReadyDiff.buildAttrChange(xpath, attributeName, (direction == CheckDirection.OLD_VS_NEW) ? newValue : oldAttributeValue));
            }
        }

        return diffsOnThisLayer;
    }

    private boolean additionNewVsOld(List<JSReadyDiff> diffsOnThisLayer, CheckDirection direction) {
        if(direction == CheckDirection.NEW_VS_OLD && diffsOnThisLayer.size() == 1) {
            return diffsOnThisLayer.get(0).isAddition();
        }
        return false;
    }

    private static boolean textDifferenceDiff(Element oldElement, Element newElement) {
        return !oldElement.getTextContent().equals(newElement.getTextContent());
    }

    private static boolean baseElementDiff(Element oldElement, Element newElement) {
        if(oldElement == null || newElement == null) return false;
        return !oldElement.getTagName().equals(newElement.getTagName());
    }

    private enum CheckDirection {
        OLD_VS_NEW,
        NEW_VS_OLD
    }
}
