package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.ElseIfElement;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.injector.tag.meta.VisibilityDetermination;
import io.getmedusa.medusa.core.injector.tag.meta.VisibilityDetermination.ConditionResult;
import io.getmedusa.medusa.core.registry.ConditionalRegistry;
import io.getmedusa.medusa.core.util.IdentifierGenerator;
import io.getmedusa.medusa.core.util.WrapperUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

public class ConditionalTag extends AbstractTag {
    private static final ConditionalRegistry CONDITIONAL_REGISTRY = ConditionalRegistry.getInstance();

    @Override
    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        Elements conditionalElements = findConditionalElements(result);
        conditionalElements.sort(Comparator.comparingInt(o -> o.getElementsByTag(TagConstants.CONDITIONAL_TAG).size()));
        for(Element conditionalElement : conditionalElements) {
            handleIfElement(variables, conditionalElement);
        }

        return result;
    }

    private Elements findConditionalElements(InjectionResult result) {
        final Elements elementsByTag = result.getDocument().getElementsByTag(TagConstants.CONDITIONAL_TAG);
        return new Elements(elementsByTag.stream().filter(this::hasNoTemplateTagParent).toList());
    }

    private boolean hasNoTemplateTagParent(Element tag) {
        for(Element parent : tag.parents()) {
            if(TagConstants.TEMPLATE_TAG.equals(parent.tagName())) {
                return false;
            }
        }
        return true;
    }

    private void handleIfElement(Map<String, Object> variables, Element conditionalElement) {
        final ConditionResult mainCondition = VisibilityDetermination.getInstance().determine(variables, conditionalElement);
        final String ifId = generateIfID(conditionalElement, mainCondition.condition());

        //find elements
        final Element elseElement = getElseElement(conditionalElement);
        final List<ElseIfElement> elseIfElements = getElseIfElements(conditionalElement, variables); //pre-wrapped
        final Elements mainElements = filterMainElements(conditionalElement, elseElement, elseIfElements);

        //wrapping
        addFullWrapperAndReplaceMIFElement(conditionalElement, ifId);
        final Element mainElementWrapper = wrapMainElement(mainElements);
        final Element defaultElseWrapper = wrapDefaultElseElement(elseElement);

        //visibility
        final ElseIfElement activeElseIf;
        if(!mainCondition.visible()) {
            activeElseIf = determineActiveElseIf(elseIfElements); //determine which elseIf is relevant, if any
        } else {
            activeElseIf = null; //not relevant, overruled by main node
        }

        hideAll(elseIfElements, activeElseIf);
        if(null == activeElseIf) {
            if (mainCondition.visible()) {
                hide(defaultElseWrapper);
            } else {
                hide(mainElementWrapper);
            }
        } else {
            hide(defaultElseWrapper);
            hide(mainElementWrapper);
        }
    }

    private void hideAll(List<ElseIfElement> elseIfElements, ElseIfElement activeElseIf) {
        for(ElseIfElement e : elseIfElements) {
            if(!e.equals(activeElseIf)) {
                hide(e.getElement());
            }
        }
    }

    private ElseIfElement determineActiveElseIf(List<ElseIfElement> elements) {
        for(ElseIfElement element : elements) if (element.isValid()) return element;
        return null;
    }

    private List<ElseIfElement> getElseIfElements(Element conditionalElement, Map<String, Object> variables) {
        final Elements elements = conditionalElement.getElementsByTag(TagConstants.M_ELSEIF);
        if(elements.isEmpty()) return Collections.emptyList();
        return elements.stream().map(e -> new ElseIfElement(e, variables)).toList();
    }

    private void addFullWrapperAndReplaceMIFElement(Element conditionalElement, String ifId) {
        final Element fullConditionalWrapper = WrapperUtils.wrapAndReplace(conditionalElement, "m-if-wrapper");
        fullConditionalWrapper.attr(TagConstants.M_IF, ifId);
    }

    private Element wrapMainElement(Elements mainElements) {
        return WrapperUtils.wrap(mainElements, "m-if-main");
    }

    private Element wrapDefaultElseElement(Element elseElement) {
        Element defaultElseWrapper = null;
        if(elseElement != null) defaultElseWrapper = WrapperUtils.wrapAndReplace(elseElement, "m-if-default-else");
        return defaultElseWrapper;
    }

    private Elements filterMainElements(Element conditionalElement, Element defaultElseElement, List<ElseIfElement> elseIfElements) {
        List<Element> elementsToFilter = new ArrayList<>();
        if(defaultElseElement != null) elementsToFilter.add(defaultElseElement);
        if(elseIfElements != null) for(ElseIfElement elseIfElement : elseIfElements) elementsToFilter.add(elseIfElement.getElement());
        return new Elements(conditionalElement.children().stream().filter(e -> !elementsToFilter.contains(e)).toList());
    }

    private Element getElseElement(Element conditionalElement) {
        final Elements elements = conditionalElement.getElementsByTag(TagConstants.M_ELSE);
        if(elements.isEmpty()) return null;
        if(elements.size() > 1) throw new IllegalStateException("m:if condition can only have one m:else condition. Use m:else if for multiple conditions.");
        return elements.get(0);
    }

    private String generateIfID(Element element, String condition) {
        final String ifID = IdentifierGenerator.generateIfID(element.html());
        CONDITIONAL_REGISTRY.add(ifID, condition);
        return ifID;
    }

    private void hide(Element elementToHide) {
        if(elementToHide == null) return;
        elementToHide.attr("style", "display:none;");
    }
}
