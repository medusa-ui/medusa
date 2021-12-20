package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.EachValueRegistry;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

public class ValueTag extends AbstractTag {

    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        handleMTextTag(result, variables, request);
        handleMValueAttribute(result, variables, request);
        return result;
    }

    private void handleMTextTag(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        //<m:text item="counter-value" /> w/ "counter-value" = 123
        //becomes
        //<span from-value="counter-value">123</span>

        Elements mTextTags = result.getDocument().getElementsByTag(TagConstants.TEXT_TAG);
        for (Element mTextTag : mTextTags) {
            final String item = mTextTag.attr(TagConstants.TEXT_TAG_ITEM_ATTR).trim();
            String variableValue = variableToString(item, variables);
            if(null == variableValue) variableValue = getPossibleEachValue(mTextTag, item, request).toString();
            mTextTag.replaceWith(createSpan(item, variableValue));
        }
    }

    private Object getPossibleEachValue(Element currentElem, String eachName, ServerRequest request) {
        Element parentWithEachName = findParentWithEachName(currentElem.parents(), eachName);
        if(null != parentWithEachName) {
            int index = Integer.parseInt(parentWithEachName.attr(TagConstants.INDEX));
            return EachValueRegistry.getInstance().get(request, eachName, index);
        }
        return eachName; //default
    }

    private Element findParentWithEachName(Elements parents, String eachName) {
        for(Element parent : parents) {
            if(parent.hasAttr(TagConstants.M_EACH) && parent.attr(TagConstants.M_EACH).equals(eachName)) return parent;
        }
        return null;
    }

    private void handleMValueAttribute(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        //<input type="text" m:value="counter-value" /> w/ "counter-value" = 123
        //becomes
        //<input type="text" from-value="counter-value" value="123" />

        Elements tagsWithMValue = result.getDocument().getElementsByAttribute("m:value");
        for (Element tagWithMValue : tagsWithMValue) {
            final String item = tagWithMValue.attr("m:value").trim();
            String variableValue = variableToString(item, variables);
            if(null == variableValue) variableValue = getPossibleEachValue(tagWithMValue, item, request).toString();
            tagWithMValue.removeAttr("m:value");
            tagWithMValue.val(variableValue);
            tagWithMValue.attr("from-value", item);
        }
    }

    private Node createSpan(String item, String variableValue) {
        return new Element(Tag.valueOf("span"), "")
                .text(variableValue)
                .attr("from-value", item);
    }
}
