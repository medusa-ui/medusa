package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.Div;
import io.getmedusa.medusa.core.injector.tag.meta.DivResolver;
import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.EachValueRegistry;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.util.EachParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

import static java.util.Collections.sort;

public class IterationTag extends AbstractTag {

    public InjectionResult inject(InjectionResult injectionResult, Map<String, Object> variables, ServerRequest request) {
        //<m:foreach collection="orders" eachName="order">
        //  <p>
        //      Product: <m:text item="order.product.name" /> Number: <m:text item="order.number" />
        //      <button m-click="cancelOrder(order.id)">Remove</button>
        //  </p>
        //</m:foreach>
        //becomes
        //<template m-id="t-625272461">
        //     <p>Product: [$this.each.product.name] Number: [$this.each.number]
        //     <button m-click="cancelOrder(order.id)">Remove</button>
        //     </p>
        //</template>
        //<div index="0" template-id="t-625272461" m-each="order">
        //  <p>Product: Whitewood Number: 5
        //  <button m-click="cancelOrder(order.id)">Remove</button>
        //  </p>
        //</div>
        //<div index="1" template-id="t-625272461">
        //     <p>Product: Darkwoods Number: 3</p>
        //</div>

        Elements foreachElements = injectionResult.getDocument().getElementsByTag(TagConstants.ITERATION_TAG);
        foreachElements.sort(Comparator.comparingInt(o -> o.getElementsByTag(TagConstants.ITERATION_TAG).size()));
        for (Element foreachElement : foreachElements) {
            Element clone = foreachElement.clone();
            final String collection = foreachElement.attr(TagConstants.ITERATION_TAG_COLLECTION_ATTR);

            final String templateID = generateTemplateID(foreachElement, collection);
            final String eachName = conditionalAttribute(foreachElement, TagConstants.ITERATION_TAG_EACH_ATTR);

            Node template = createTemplate(templateID, clone);
            foreachElement.children().remove();
            foreachElement.appendChild(template);

            Object conditionParsed = parseConditionWithVariables(collection, variables);
            if (conditionParsed instanceof Collection) {
                //a true 'foreach', the 'each' becomes each element of the collection
                Object[] arrayCondition = conditionAsArray(conditionParsed);
                for (int i = 0; i < arrayCondition.length; i++) {
                    Object obj = arrayCondition[i];
                    handleIteration(foreachElement, template, i, obj, templateID, eachName, request);
                }
            } else {
                //condition parsed is a single value, to which we count up
                long iterationCondition = Long.parseLong(conditionParsed.toString());
                for (int i = 0; i < iterationCondition; i++) {
                    handleIteration(foreachElement, template, i, i, templateID, eachName, request);
                }
            }
            foreachElement.unwrap();
        }

        labelNestedTemplates(injectionResult.getDocument());

        return injectionResult;
    }

    private void labelNestedTemplates(Document document) {
        Elements templates = document.getElementsByTag("template");
        templates.sort(Comparator.comparingInt(o -> o.getElementsByTag("template").size()));
        for(Element template : templates) {
            Element templateParent = findTemplateParent(template.parents());
            if(templateParent != null) {
                final String originalTemplateId = template.attr(TagConstants.M_ID);
                final String templateAttributeId = templateParent.attr(TagConstants.M_ID) + "#" + originalTemplateId;
                template.attr(TagConstants.M_ID, templateAttributeId);

                Elements divs = document.getElementsByAttributeValue(TagConstants.TEMPLATE_ID, originalTemplateId);
                for(Element div : divs) {
                    div.attr(TagConstants.TEMPLATE_ID, templateAttributeId);
                }
            }
        }
    }

    private Element findTemplateParent(Elements parents) {
        for(Element template : parents) {
            if(template.hasAttr(TagConstants.M_ID)){
                return template;
            }
        }
        return null;
    }

    private Node createTemplate(String templateID, Element foreachElement) {
        Element node = new Element(Tag.valueOf("template"), "")
                .attr(TagConstants.M_ID, templateID);
        node.appendChildren(foreachElement.children());
        return node;
    }

    private void handleIteration(Element foreachElement, Node template, int index, Object obj, String templateId, String eachName, ServerRequest request) {
        if(null != eachName) EachValueRegistry.getInstance().add(request, eachName, index, obj);
        foreachElement.appendChild(createDiv(template, index, templateId, eachName));
    }

    private String generateTemplateID(Element foreachElement, String collection) {
        String tId = "t-" + Math.abs(foreachElement.html().hashCode());
        IterationRegistry.getInstance().add(tId, collection);
        return tId;
    }

    private Node createDiv(Node template, int index, String templateId, String eachName) {
        Element element = new Element(Tag.valueOf("div"), "")
                .attr(TagConstants.INDEX, Integer.toString(index))
                .attr(TagConstants.TEMPLATE_ID, templateId);

        if(eachName != null) element = element.attr(TagConstants.M_EACH, eachName);

        element.appendChildren(template.childNodesCopy());
        element.getElementsByTag("template").remove();

        return element;
    }

    private String conditionalAttribute(Element e, String attr) {
        if (e.hasAttr(attr)) return e.attr(attr);
        return null;
    }


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
        for (ForEachElement element : depthElements) {
            if (element.hasParent()) {
                element.getParent().addChild(element);
            }
        }
    }

    private List<Div> buildDivs(Map<String, Object> variables, List<ForEachElement> depthElements) {
        List<Div> complexDivStructure = new ArrayList<>();
        for (ForEachElement element : depthElements) {
            //only run on the parent layer, as this code will traverse through children itself
            if (element.getDepth() == 0) {
                Object conditionParsed = parseConditionWithVariables(element.condition, variables);
                if (conditionParsed instanceof Collection) {
                    //a true 'foreach', the 'each' becomes each element of the collection
                    Object[] iterationCondition = conditionAsArray(conditionParsed);
                    Div overallParent = new Div(element);
                    complexDivStructure.add(overallParent);

                    for (Object eachObject : iterationCondition) {
                        complexDivStructure.addAll(createDivForChildren(variables, element, new Div(element, eachObject, overallParent)));
                    }

                } else {
                    //condition parsed is a single value, to which we count up
                    Long iterationCondition = Long.parseLong(conditionParsed.toString());
                    Div overallParent = new Div(element);
                    complexDivStructure.add(overallParent);

                    for (int i = 0; i < iterationCondition; i++) {
                        complexDivStructure.addAll(createDivForChildren(variables, element, new Div(element, i, overallParent)));
                    }
                }
            }
        }
        return complexDivStructure;
    }

    private List<Div> createDivForChildren(Map<String, Object> variables, ForEachElement element, Div parentDiv) {
        List<Div> complexDivStructure = new ArrayList<>();
        complexDivStructure.add(parentDiv);

        if (element.hasChildren()) {
            for (ForEachElement child : element.getChildren()) {
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

                if (!hasChildElement) {
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
        for (Div div : complexStructure) {
            div.setResolvedHTML(div.getElement().getInnerHTML());
            Div parentDiv = div.getParent();
            if (parentDiv != null) parentDiv.getChildren().add(div);
            if (div.isRoot()) rootElements.add(div);
        }

        //then go over all root divs
        //and build up the HTML parent -> child
        for (Div root : rootElements) {
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
        for (Div child : root.getChildren()) {
            builder.append(child.getResolvedHTML());
        }
        return builder.toString();
    }

    private String deepResolve(Div div) {
        Map<String, StringBuilder> childMap = new HashMap<>();
        for (Div child : div.getChildren()) {
            StringBuilder builder = childMap.getOrDefault(child.getElement().blockHTML, new StringBuilder());
            builder.append(deepResolve(child));
            childMap.put(child.getElement().blockHTML, builder);
        }

        for (Map.Entry<String, StringBuilder> entry : childMap.entrySet()) {
            div.setResolvedHTML(div.getResolvedHTML().replace(entry.getKey(), entry.getValue().toString()));
        }

        div.setResolvedHTML(DivResolver.resolve(div));
        return div.getResolvedHTML();
    }

    @SuppressWarnings("unchecked")
    private Object[] conditionAsArray(Object conditionParsed) {
        return ((Collection<Object>) conditionParsed).toArray();
    }
}
