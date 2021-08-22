package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.ForEachDiv;
import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement.RenderInfo;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.util.EachParser;
import io.getmedusa.medusa.core.util.ExpressionEval;
import io.getmedusa.medusa.core.util.IdentifierGenerator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IterationTag {

    private static final String TAG_EACH = "[$each";
    private static final String TAG_THIS_EACH = "[$this.each";

    private static final EachParser PARSER = new EachParser();
    private static final Pattern PROPERTY_PATTERN =  Pattern.compile("\\[\\$(this\\.|(parent\\.){1,99})each\\.?.*?]", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult injectionResult, Map<String, Object> variables) {
        String html = injectionResult.getHtml();

        //find all the foreaches, split them up and order them as deepest-first
        final List<ForEachElement> depthElements = PARSER.buildDepthElements(html);
        Collections.sort(depthElements);
        for(ForEachElement depthElement : depthElements) {
            ForEachElement parent = depthElement.getParent();
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
        final String templateID = IdentifierGenerator.generateTemplateID(element.blockHTML);
        IterationRegistry.getInstance().add(templateID, element.condition);
        final RenderInfo renderInfo = new RenderInfo(element.blockHTML, templateID);
        renderInfo.template = "<template m-id=\"" + templateID + "\">" + element.innerHTML.replace(TAG_EACH, TAG_THIS_EACH) + "</template>\n";

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
                final String divInnerBlock = parseEach(element, variables, eachObject, index);
                divs.add(new ForEachDiv(index, templateID, divInnerBlock).toString());
            }
        } else {
            //if only a single value, we don't need real 'foreach' but rather a regular loop
            //the 'each' then becomes an index
            for (int index = 0; index < (int) conditionParsed; index++) {
                final String divInnerBlock = parseEach(element, variables, index, index);
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
    private String parseEach(ForEachElement element, Map<String, Object> variables, Object eachObject, int index) {
        String divContent = element.innerHTML;
        divContent = divContent.replace(TAG_EACH, TAG_THIS_EACH); //the use of [$each] is optional and is equal to using [$this.each]

        divContent = divContent.replace(TAG_THIS_EACH + ']', eachObject.toString());

        Matcher matcher = PROPERTY_PATTERN.matcher(divContent);
        while (matcher.find()) {
            final String replace = matcher.group(); // $[this.each.property] or $[parent.parent.each.property]
            //so everything up to the .each. determines what the eachObject is going to be used in this expression

            final String eachPropertyDeterminator = replace.substring(2, replace.indexOf(".each"));
            Object transitiveEachObject = eachObject;
            if(!"this".equals(eachPropertyDeterminator)) {
                ForEachElement parentElem = ExpressionEval.evalForEachElement(eachPropertyDeterminator, element);
                if(null != parentElem) {
                    Object conditionParsed = parseConditionWithVariables(parentElem.condition, variables);
                    @SuppressWarnings("unchecked") Object[] iterationCondition = ((Collection<Object>)conditionParsed).toArray();
                    try {
                        transitiveEachObject = iterationCondition[index];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new IndexOutOfBoundsException("Calling a nested loop each via ["+eachPropertyDeterminator+"], but index ["+index+"] does not reach that far."); //TODO clearer error
                    }
                }
            }
            String evalObject = transitiveEachObject.toString();
            if(replace.contains(".each.")) {
                final String property = replace.substring(replace.indexOf(".each.") + 6, replace.length() - 1).trim();
                evalObject = ExpressionEval.evalObject(property, transitiveEachObject);
            }
            divContent = divContent.replace(replace, evalObject);
        }

        return divContent;
    }

    private Object parseConditionWithVariables(String condition, Map<String, Object> variables) {
        final Object variable = variables.getOrDefault(condition, new ArrayList<>());
        if(variable == null) return Collections.emptyList();
        return variable;
    }


}
