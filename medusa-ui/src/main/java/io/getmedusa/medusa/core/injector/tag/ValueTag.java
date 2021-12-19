package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.util.Map;

public class ValueTag extends AbstractTag {

    public InjectionResult inject(InjectionResult result, Map<String, Object> variables) {
        handleMTextTag(result, variables);
        handleMValueAttribute(result, variables);
        return result;
    }

    private void handleMTextTag(InjectionResult result, Map<String, Object> variables) {
        //<m:text item="counter-value" /> w/ "counter-value" = 123
        //becomes
        //<span from-value="counter-value">123</span>

        Elements mTextTags = result.getDocument().getElementsByTag("m:text");
        for (Element mTextTag : mTextTags) {
            final String item = mTextTag.attr("item").trim();
            final String variableValue = variableToString(item, variables);
            mTextTag.replaceWith(createSpan(item, variableValue));
        }
    }

    private void handleMValueAttribute(InjectionResult result, Map<String, Object> variables) {
        //<input type="text" m:value="counter-value" /> w/ "counter-value" = 123
        //becomes
        //<input type="text" from-value="counter-value" value="123" />

        Elements tagsWithMValue = result.getDocument().getElementsByAttribute("m:value");
        for (Element tagWithMValue : tagsWithMValue) {
            final String item = tagWithMValue.attr("m:value").trim();
            final String variableValue = variableToString(item, variables);
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
