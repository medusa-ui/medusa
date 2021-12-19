package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.Div;
import io.getmedusa.medusa.core.injector.tag.meta.DivResolver;
import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.util.EachParser;

import java.util.*;

import static java.util.Collections.sort;

public class IterationTag {

    private static final EachParser PARSER = new EachParser();

    public InjectionResult injectWithVariables(InjectionResult injectionResult, Map<String, Object> variables) {
        String html = injectionResult.getHTML();

        //find all the foreaches, split them up and order them as deepest-first
        final List<ForEachElement> depthElements = PARSER.buildDepthElements(html);

        //prep the foreach elements with children, sort them and interpret each div level
        //this is still per condition, so 2 foreach with each 3 elements = 2 foreach element
        addChildren(depthElements);
        sort(depthElements);

        //interpret the conditions and make div per iteration, so 2 foreach with each 3 elements = 9 divs (3*2 child divs + 2 parent divs + 1 overall div)
        //this also allows us to walk through a parent chain on each level
        List<Div> complexStructure = buildDivs(variables, depthElements);
        sort(complexStructure);

        //finally, render the full complex object
        //child elements get merged together until parent elements can be merged into the html
        html = renderHTML(html, complexStructure);

        //injectionResult.setHtml(html);
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
                    Div overallParent = new Div(element);
                    complexDivStructure.add(overallParent);

                    for (Object eachObject : iterationCondition) {
                        complexDivStructure.addAll(createDivForChildren(variables, element, new Div(element, eachObject, overallParent) ));
                    }

                } else {
                    //condition parsed is a single value, to which we count up
                    Long iterationCondition = Long.parseLong(conditionParsed.toString());
                    Div overallParent = new Div(element);
                    complexDivStructure.add(overallParent);

                    for (int i = 0; i < iterationCondition; i++) {
                        complexDivStructure.addAll(createDivForChildren(variables, element, new Div(element, i, overallParent) ));
                    }
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
                final boolean hasChildElement;
                if (childConditionParsed instanceof Collection) {
                    //a true 'foreach', the 'each' becomes each element of the collection
                    Object[] childIterationCondition = conditionAsArray(childConditionParsed);
                    hasChildElement = childIterationCondition.length > 0;
                    for (Object childEachObject : childIterationCondition) {
                        Div div = new Div(child, childEachObject, parentDiv);
                        complexDivStructure.addAll(createDivForChildren(variables, child, div));
                    }
                } else {
                    //condition parsed is a single value, to which we count up
                    long iterationCondition = Long.parseLong(childConditionParsed.toString());
                    hasChildElement = iterationCondition > 0;
                    for (int i = 0; i < iterationCondition; i++) {
                        Div div = new Div(child, i, parentDiv);
                        complexDivStructure.addAll(createDivForChildren(variables, child, div));
                    }
                }

                if(!hasChildElement) {
                    ForEachElement empty = new ForEachElement(child.blockHTML, "");
                    empty.setParent(child.getParent());
                    Div div = new Div(empty, null, parentDiv);
                    complexDivStructure.add(div);
                }
            }
        }
        return complexDivStructure;
    }

    private String renderHTML(String html, List<Div> complexStructure) {
        List<Div> rootElements = new ArrayList<>();
        //go over all elements of the structure to
        //a. resolve depth-first
        //b. add parent/child references
        //c. take note of which divs are root divs
        for(Div div : complexStructure) {
            div.setResolvedHTML(div.getElement().getInnerHTML());
            Div parentDiv = div.getParent();
            if(parentDiv != null) parentDiv.getChildren().add(div);
            if(div.isRoot()) rootElements.add(div);
        }

        //then go over all root divs
        //and build up the HTML parent -> child
        for(Div root : rootElements) {
            //build template based on the element (parent -> child)
            String template = DivResolver.buildTemplate(root.getElement());

            //resolve child elements via recursion
            deepResolve(root);

            //all elements should now be resolved, so they just need to be pasted together
            String wrappedDiv = DivResolver.wrapInDiv(root, buildFinalHTML(root));
            html = html.replace(root.getElement().blockHTML, template + wrappedDiv);
        }

        return html;
    }

    private String buildFinalHTML(Div root) {
        StringBuilder builder = new StringBuilder();
        for(Div child : root.getChildren()) {
            builder.append(child.getResolvedHTML());
        }
        return builder.toString();
    }

    private String deepResolve(Div div) {
        Map<String, StringBuilder> childMap = new HashMap<>();
        for(Div child : div.getChildren()) {
            StringBuilder builder = childMap.getOrDefault(child.getElement().blockHTML, new StringBuilder());
            builder.append(deepResolve(child));
            childMap.put(child.getElement().blockHTML, builder);
        }

        for(Map.Entry<String, StringBuilder> entry : childMap.entrySet()) {
            div.setResolvedHTML(div.getResolvedHTML().replace(entry.getKey(), entry.getValue().toString()));
        }

        div.setResolvedHTML(DivResolver.resolve(div));

        return div.getResolvedHTML();
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
