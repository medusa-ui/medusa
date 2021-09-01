package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.Div;
import io.getmedusa.medusa.core.injector.tag.meta.DivResolver;
import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.util.EachParser;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.Collections.sort;

public class Iteration2Tag {

    private static final String TAG_EACH = "[$each";
    private static final String TAG_THIS_EACH = "[$this.each";

    private static final EachParser PARSER = new EachParser();
    private static final Pattern PROPERTY_PATTERN =  Pattern.compile("\\[\\$(this\\.|(parent\\.){1,99})each\\.?.*?]", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult injectionResult, Map<String, Object> variables) {
        String html = injectionResult.getHtml();

        //find all the foreaches, split them up and order them as deepest-first
        final List<ForEachElement> depthElements = PARSER.buildDepthElements(html);

        //prep the foreach elements with children, sort them and interpret each div level
        //this is still per condition, so 2 foreach with each 3 elements = 2 foreach element
        addChildren(depthElements);
        sort(depthElements);

        //interpret the conditions and make div per iteration, so 2 foreach with each 3 elements = 8 divs (3*2 child divs + 2 parent divs)
        //this also allows us to walk through a parent chain on each level
        List<Div> complexStructure = buildDivs(variables, depthElements);
        sort(complexStructure);

        //finally, render the full complex object
        //child elements get merged together until parent elements can be merged into the html
        html = renderHTML(html, complexStructure);

        injectionResult.setHtml(html);
        return injectionResult;
    }

    private void addChildren(List<ForEachElement> depthElements) {
        for(ForEachElement element : depthElements) {
            if(element.hasParent()) {
                element.getParent().addChild(element);
            }
        }
    }

    private List<Div> buildDivs(Map<String, Object> variables, List<ForEachElement> depthElements) {
        List<Div> complexDivStructure = new ArrayList<>();
        for(ForEachElement element : depthElements) {
            //only run on the parent layer, as this code will traverse through children itself
            if(element.getDepth() == 0) {
                Object conditionParsed = parseConditionWithVariables(element.condition, variables);
                if (conditionParsed instanceof Collection) {
                    //a true 'foreach', the 'each' becomes each element of the collection
                    Object[] iterationCondition = conditionAsArray(conditionParsed);
                    for (Object eachObject : iterationCondition) {
                        complexDivStructure.addAll(createDivForChildren(variables, element, new Div(element, eachObject)));
                    }
                } else {
                    throw new RuntimeException("Not yet implemented");
                }
            }
        }
        return complexDivStructure;
    }

    private List<Div> createDivForChildren(Map<String, Object> variables, ForEachElement element, Div parentDiv) {
        List<Div> complexDivStructure = new ArrayList<>();
        complexDivStructure.add(parentDiv);

        if(element.hasChildren()) {
            for(ForEachElement child : element.getChildren()) {
                Object childConditionParsed = parseConditionWithVariables(child.condition, variables);
                if (childConditionParsed instanceof Collection) {
                    //a true 'foreach', the 'each' becomes each element of the collection
                    Object[] childIterationCondition = conditionAsArray(childConditionParsed);
                    for (Object childEachObject : childIterationCondition) {
                        Div div = new Div(child, childEachObject, parentDiv);
                        complexDivStructure.addAll(createDivForChildren(variables, child, div));
                    }
                } else {
                    throw new RuntimeException("Not yet implemented");
                }
            }
        }
        return complexDivStructure;
    }

    private String renderHTML(String html, List<Div> complexStructure) {
        for(Div div : complexStructure) {
            if(div.hasParent()) {
                div.setResolvedHTML(DivResolver.resolve(div));

                final String parentInnerHTML = div.getParent().getElement().innerHTML;
                final String divContent = parentInnerHTML.replace(parentInnerHTML, div.getResolvedHTML());

                div.getParent().appendToResolvedHTML(DivResolver.wrapInDiv(div, divContent)); //merge
            } else {
                System.out.println("Final: " + div.getResolvedHTML());
                //html = html.replace(div.getHtml(), div.render());
            }
        }
        return html;
    }

    @SuppressWarnings("unchecked")
    private Object[] conditionAsArray(Object conditionParsed) {
        return ((Collection<Object>) conditionParsed).toArray();
    }

    private Object parseConditionWithVariables(String condition, Map<String, Object> variables) {
        final Object variable = variables.getOrDefault(condition, new ArrayList<>());
        if(variable == null) return Collections.emptyList();
        return variable;
    }

}
