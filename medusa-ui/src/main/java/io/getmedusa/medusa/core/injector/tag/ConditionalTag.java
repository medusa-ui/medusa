package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.ElseIfElement;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.injector.tag.meta.VisibilityDetermination;
import io.getmedusa.medusa.core.injector.tag.meta.VisibilityDetermination.ConditionResult;
import io.getmedusa.medusa.core.registry.ConditionalRegistry;
import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.util.IdentifierGenerator;
import io.getmedusa.medusa.core.util.WrapperUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.*;

public class ConditionalTag extends AbstractTag {
    private static final ConditionalRegistry CONDITIONAL_REGISTRY = ConditionalRegistry.getInstance();
    public static final String ID_SUFFIX_ELSE_IF = "e";
    public static final String ID_SUFFIX_IF = "i";
    public static final String ID_SUFFIX_MAIN = "m";

    @Override
    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        Elements conditionalElements = result.getDocument().select(TagConstants.CONDITIONAL_TAG);
        conditionalElements.sort(Comparator.comparingInt(o -> o.select(TagConstants.CONDITIONAL_TAG).size()));
        for(Element conditionalElement : conditionalElements) {
            handleIfElement(variables, conditionalElement, request);
        }

        return result;
    }

    private void handleIfElement(Map<String, Object> variables, Element conditionalElement, ServerRequest request) {
        final ConditionResult mainCondition = VisibilityDetermination.getInstance().determine(variables, conditionalElement, request);

        List<String> conditions = new ArrayList<>();
        conditions.add(mainCondition.condition());

        //find elements
        final Element elseElement = getElseElement(conditionalElement);
        final List<ElseIfElement> elseIfElements = getElseIfElements(conditionalElement, variables, request, mainCondition.condition()); //pre-wrapped
        final Elements mainElements = filterMainElements(conditionalElement, elseElement, elseIfElements);

        for(ElseIfElement elseIfElement : elseIfElements) {
            conditions.add(elseIfElement.getCondition());
            addIfId(elseIfElement.getElement(), ID_SUFFIX_ELSE_IF, elseIfElement.getCondition());
        }

        //wrapping
        WrapperUtils.wrapAndReplace(conditionalElement, "m-if-wrapper");
        final Element mainElementWrapper = wrapMainElement(mainElements, mainCondition.condition());
        final Element defaultElseWrapper = wrapDefaultElseElement(elseElement);
        addIfId(defaultElseWrapper, ID_SUFFIX_IF, oppositeOfCombinedConditions(conditions));

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

    public String oppositeOfCombinedConditions(List<String> conditions) {
        StringBuilder condition = new StringBuilder("!(");
        String appender = "";
        for(String c : conditions) {
            condition.append(appender);
            condition.append(c);
            appender = " || ";

        }

        condition.append(")");
        return condition.toString();
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

    private List<ElseIfElement> getElseIfElements(Element conditionalElement, Map<String, Object> variables, ServerRequest request, String mainCondition) {
        final Elements elements = conditionalElement.select(TagConstants.M_ELSEIF);
        if(elements.isEmpty()) return Collections.emptyList();
        return elements.stream().map(e -> new ElseIfElement(e, variables, request, mainCondition)).toList();
    }

    private Element wrapMainElement(Elements mainElements, String condition) {
        Element mainWrapper = WrapperUtils.wrap(mainElements, "m-if-main");
        addIfId(mainWrapper, ID_SUFFIX_MAIN, condition);
        return mainWrapper;
    }

    private Element wrapDefaultElseElement(Element elseElement) {
        Element defaultElseWrapper = null;
        if(elseElement != null) defaultElseWrapper = WrapperUtils.wrapAndReplace(elseElement, "m-if-default-else");
        return defaultElseWrapper;
    }

    private Element addIfId(Element element, String seed, String condition) {
        if(element == null) return null;
        element.attr(TagConstants.M_IF, generateIfID(element, seed, condition));
        return element;
    }

    private Elements filterMainElements(Element conditionalElement, Element defaultElseElement, List<ElseIfElement> elseIfElements) {
        List<Element> elementsToFilter = new ArrayList<>();
        if(defaultElseElement != null) elementsToFilter.add(defaultElseElement);
        if(elseIfElements != null) for(ElseIfElement elseIfElement : elseIfElements) elementsToFilter.add(elseIfElement.getElement());
        return new Elements(conditionalElement.children().stream().filter(e -> !elementsToFilter.contains(e)).toList());
    }

    private Element getElseElement(Element conditionalElement) {
        final Elements elements = conditionalElement.select(TagConstants.M_ELSE);
        if(elements.isEmpty()) return null;
        if(elements.size() > 1) throw new IllegalStateException("m:if condition can only have one m:else condition. Use m:else if for multiple conditions.");
        return elements.get(0);
    }

    private String generateIfID(Element element, String suffix, String condition) {
        final String ifID = IdentifierGenerator.generateIfID(suffix, element.html());

        //check if the condition here contains a reference to any of the wrapping each values
        Map<String, String> wrappingEachValues = findWrappingEachValues(element.parents());
        condition = replaceConditionValues(condition, wrappingEachValues);
        CONDITIONAL_REGISTRY.add(ifID, condition);
        return ifID;
    }

    private String replaceConditionValues(String condition, Map<String, String> wrappingEachValues) {
        if(wrappingEachValues.isEmpty()) return condition;
        for(Map.Entry<String, String> eachEntry : wrappingEachValues.entrySet()) {
            condition = condition.replace(eachEntry.getKey(), eachEntry.getValue());
        }
        return condition;
    }

    private Map<String, String> findWrappingEachValues(Elements parents) {
        Map<String, String> wrappingValues = new HashMap<>();

        for(Element parent : parents) {
            if(parent.hasAttr(TagConstants.M_EACH)) {
                final String eachName = parent.attr(TagConstants.M_EACH);
                final String templateId = parent.attr(TagConstants.TEMPLATE_ID);
                final String iterationItem = IterationRegistry.getInstance().findByTemplateId(templateId);
                wrappingValues.put(eachName, iterationItem + "[$index#" + eachName + "]");
            }
        }

        return wrappingValues;
    }

    private void hide(Element elementToHide) {
        if(elementToHide == null) return;
        elementToHide.attr("style", "display:none;");
    }
}
