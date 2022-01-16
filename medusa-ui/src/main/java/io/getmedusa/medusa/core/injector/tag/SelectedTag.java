package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

public class SelectedTag extends AbstractTag {

    @Override
    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        Elements mSelectedList = result.getDocument().getElementsByAttribute(TagConstants.M_SELECTED);
        for(Element mSelected : mSelectedList) {
            final String selectedItem = mSelected.attr(TagConstants.M_SELECTED);

            Object variableValue = getPossibleEachValue(mSelected, selectedItem, request, variables);
            if(null == variableValue) variableValue = variableToString(selectedItem, variables);
            if(null == variableValue) variableValue = selectedItem;

            mSelected.removeAttr(TagConstants.M_SELECTED);
            Elements options = mSelected.getElementsByTag("option");
            for(Element option : options) {
                if(variableValue.toString().equals(option.val())) {
                    option.attr(TagConstants.M_SELECTED_REPLACEMENT, TagConstants.M_SELECTED_REPLACEMENT);
                } else {
                    option.removeAttr(TagConstants.M_SELECTED_REPLACEMENT);
                }
            }
        }
        return result;
    }
}
