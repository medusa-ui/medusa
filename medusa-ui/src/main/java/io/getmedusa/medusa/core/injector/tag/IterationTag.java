package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.EachValueRegistry;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

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

    @SuppressWarnings("unchecked")
    private Object[] conditionAsArray(Object conditionParsed) {
        return ((Collection<Object>) conditionParsed).toArray();
    }

}
