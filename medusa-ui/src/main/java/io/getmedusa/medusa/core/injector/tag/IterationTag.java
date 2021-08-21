package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.ForEachDiv;
import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement.RenderInfo;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.util.IdentifierGenerator;
import io.getmedusa.medusa.core.util.NestedForEachParser;

import java.util.*;

public class IterationTag {

    private static final String TAG_EACH = "[$each]";
    private static final String TAG_THIS_EACH = "[$this.each]";

    private NestedForEachParser parser = new NestedForEachParser();

    public InjectionResult injectWithVariables(InjectionResult injectionResult, Map<String, Object> variables) {
        String html = injectionResult.getHtml();

        //find all the foreaches, split them up and order them as deepest-first
        final Set<ForEachElement> depthElements = parser.buildDepthElements(html);

        for(ForEachElement depthElement : depthElements) {
            ForEachElement parent = depthElement.parent;
            RenderInfo renderInfo = buildBlockReplacement(depthElement, variables);

            if(parent != null) {
                parent.setChildRenderInfo(renderInfo.merge(depthElement.getChildRenderInfo()));
            } else {
                html = html.replace(depthElement.blockHTML, depthElement.renderWithChildRenders(renderInfo));
            }
        }

        injectionResult.setHtml(html);
        return injectionResult;
    }

    //code for replacing a foreach block
    private RenderInfo buildBlockReplacement(ForEachElement element, Map<String, Object> variables) {
        final RenderInfo renderInfo = new RenderInfo(element.blockHTML);
        final String templateID = IdentifierGenerator.generateTemplateID(element.blockHTML);
        renderInfo.template = "<template m-id=\"" + templateID + "\">" + element.innerHTML + "</template>\n";

        final StringBuilder divsToRender = new StringBuilder();
        List<String> divs = turnTemplateIntoDiv(element, variables, templateID);
        for (String div : divs) divsToRender.append(div);
        renderInfo.divs = divsToRender.toString();

        return renderInfo;
    }

    //specifically for turning templates with conditions into concrete divs
    private List<String> turnTemplateIntoDiv(ForEachElement element, Map<String, Object> variables, String templateID) {
        List<String> divs = new ArrayList<>();
        Object conditionParsed = parseConditionWithVariables(element.condition, variables);
        if (conditionParsed instanceof Collection) {
            //a true 'foreach', the 'each' becomes each element of the collection
            @SuppressWarnings("unchecked") Object[] iterationCondition = ((Collection<Object>)conditionParsed).toArray();
            for (int index = 0; index < iterationCondition.length; index++) {
                Object eachObject = iterationCondition[index];
                final String divInnerBlock = parseEach(element, variables, eachObject);
                divs.add(new ForEachDiv(index, templateID, divInnerBlock).toString());
            }
        } else {
            //if only a single value, we don't need real 'foreach' but rather a regular loop
            //the 'each' then becomes an index
            for (int index = 0; index < (int) conditionParsed; index++) {
                final String divInnerBlock = parseEach(element, variables, element.innerHTML);
                divs.add(new ForEachDiv(index, templateID, divInnerBlock).toString());
            }
        }
        return divs;
    }

    /**
     * Handles the inner block of an iteration div
     * Specifically includes logic for the 'each' tags
     * @param element
     * @param variables
     * @param eachObject
     * @return String of the HTML to put into this individual div element
     */
    private String parseEach(ForEachElement element, Map<String, Object> variables, Object eachObject) {
        String divContent = element.innerHTML;
        divContent = divContent.replace(TAG_EACH, TAG_THIS_EACH); //the use of [$each] is optional and is equal to using [$this.each]

        divContent = divContent.replace(TAG_THIS_EACH, eachObject.toString());
        //TODO:: properties of each objects

        return divContent;
    }

    /*
        Matcher matcher = propertyPattern.matcher(result);
        while (matcher.find()) {
            final String replace = matcher.group(); // $[each.property]
            final String group = matcher.group(1);  // property]
            final String property = group.substring(0, group.length()-1);  // property
            result = result.replace(replace, ExpressionEval.evalObject(property, value));
    */

    private Object parseConditionWithVariables(String condition, Map<String, Object> variables) {
        final Object variable = variables.getOrDefault(condition, new ArrayList<>());
        if(variable == null) return Collections.emptyList();
        return variable;
    }


}
