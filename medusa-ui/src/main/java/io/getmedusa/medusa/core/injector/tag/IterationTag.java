package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.EachValueRegistry;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import static io.getmedusa.medusa.core.injector.tag.TagConstants.*;

public class IterationTag extends AbstractTag {

    @Override
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

        Elements foreachElements = injectionResult.getDocument().select(ITERATION_TAG);
        foreachElements.sort(Comparator.comparingInt(o -> o.select(ITERATION_TAG).size()));
        for (final Element foreachElement : foreachElements) {
            if(foreachElement == null) continue;

            String parentTagName = getParentTagName(foreachElement);
            Attributes wrapperAttributes = new Attributes();
            if(isParentIsTBody(foreachElement, parentTagName)) {
                wrapperAttributes = removeTBodyAsWrapperIfPresent(foreachElement);
                parentTagName = "table";
            }

            Element clone = foreachElement.clone();
            final String collection = foreachElement.attr(ITERATION_TAG_COLLECTION_ATTR);

            final String templateID = generateTemplateID(foreachElement, collection);
            final String eachName = conditionalAttribute(foreachElement, ITERATION_TAG_EACH_ATTR);

            Node template = createTemplate(templateID, clone, eachName, parentTagName, wrapperAttributes);
            foreachElement.children().remove();
            foreachElement.text("");
            foreachElement.appendChild(template);

            Object conditionParsed = parseConditionWithVariables(collection, variables);
            if (conditionParsed instanceof Collection || conditionParsed instanceof Map || conditionParsed.getClass().isArray()) {
                final Object[] arrayCondition;
                if(conditionParsed instanceof Collection) {
                    //a true 'foreach', the 'each' becomes each element of the collection
                    arrayCondition = conditionAsArray(conditionParsed);
                } else if(conditionParsed instanceof Map) {
                    //in a map, each 'each' becomes an entry of the map
                    arrayCondition = conditionAsMapEntryArray(conditionParsed);
                } else {
                    arrayCondition = (Object[]) conditionParsed;
                }

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

    private boolean isParentIsTBody(Element foreachElement, String parentTagName) {
        return parentTagName.equals("tbody") && foreachElement.hasParent();
    }

    private Attributes removeTBodyAsWrapperIfPresent(Element foreachElement) {
        Attributes attributes = new Attributes();
        Element oldParent = foreachElement.parent();
        if(oldParent != null && oldParent.hasParent()) {
            Element newParent = oldParent.parent();
            attributes = oldParent.attributes();
            while (!oldParent.childNodes().isEmpty()) {
                newParent.appendChild(oldParent.childNodes().get(0));
            }
            oldParent.remove();
        }
        return attributes;
    }

    private String getParentTagName(Element foreachElement) {
        if(null != foreachElement && null != foreachElement.parent()) {
            return foreachElement.parent().tagName();
        }
        return "";

    }

    private void labelNestedTemplates(Document document) {
        Elements templates = document.getElementsByTag(TEMPLATE_TAG);
        templates.sort((o1, o2) -> o2.getElementsByTag(TEMPLATE_TAG).size() - o1.getElementsByTag(TEMPLATE_TAG).size());
        for(Element template : templates) {
            Element templateParent = findTemplateParent(template.parents());
            if(templateParent != null) {
                final String originalTemplateId = template.attr(M_ID);
                final String templateAttributeId = templateParent.attr(M_ID) + "#" + originalTemplateId;
                template.attr(M_ID, templateAttributeId);

                Elements divs = document.getElementsByAttributeValue(TEMPLATE_ID, originalTemplateId);
                for(Element div : divs) {
                    div.attr(TEMPLATE_ID, templateAttributeId);
                }
            }
        }
    }

    private Element findTemplateParent(Elements parents) {
        for(Element template : parents) {
            if(template.hasAttr(M_ID)){
                return template;
            }
        }
        return null;
    }

    private Node createTemplate(String templateID, Element foreachElement, String eachName, String parentTagName, Attributes wrapperAttributes) {
        Element node = new Element(Tag.valueOf(TEMPLATE_TAG), "")
                .attr(M_ID, templateID);

        Element divWrapper = new Element("div");
        if("table".equals(parentTagName)) {
            divWrapper = new Element("tbody");
        }
        divWrapper.attributes().addAll(wrapperAttributes);

        if(eachName != null) divWrapper.attr("m-each", eachName);
        divWrapper.attr(TEMPLATE_ID, templateID);

        divWrapper.appendChildren(foreachElement.childNodesCopy());
        node.appendChild(divWrapper);

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

    private Element createDiv(Node template, int index, String templateId, String eachName) {
        Element element = (Element) template.childNodesCopy().get(0);
        element.attr(INDEX, Integer.toString(index))
                .attr(TEMPLATE_ID, templateId);

        if(eachName != null) element = element.attr(M_EACH, eachName);

        element.getElementsByTag(TEMPLATE_TAG).remove();

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

    private Object[] conditionAsMapEntryArray(Object conditionParsed) {
        Map<Object, Object> conditionMap = (Map<Object, Object>) conditionParsed;
        return conditionMap.entrySet().toArray();
    }

}
