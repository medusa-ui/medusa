package io.getmedusa.medusa.core.websocket;

import io.getmedusa.medusa.core.injector.DOMChanges.DOMChange;
import io.getmedusa.medusa.core.registry.*;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.*;
import java.util.stream.Collectors;

public class DomChangesExecution {

    private static final ConditionalRegistry CONDITIONAL_REGISTRY = ConditionalRegistry.getInstance();
    private static final ConditionalClassRegistry CONDITIONAL_CLASS_REGISTRY = ConditionalClassRegistry.getInstance();

    public List<DOMChange> process(WebSocketSession session, List<DOMChange> domChanges) {
        evaluateTitleChange(session, domChanges);
        evaluateIterationChange(domChanges);
        evaluateConditionalChange(domChanges);
        evaluateConditionalClassChange(domChanges);
        evaluateGenericMAttributesChanged(domChanges);
        processApplicableDOMChanges(domChanges);
        return domChanges;
    }

    private void processApplicableDOMChanges(List<DOMChange> domChanges) {
        List<DOMChange> changesWithScopeAll = domChanges
                .stream()
                .filter(DOMChange::isApplicable)
                .map(domChange -> new DOMChange(domChange.getF(), domChange.getV()))
                .collect(Collectors.toList());

        ActiveSessionRegistry.getInstance().sendToAll(changesWithScopeAll);
    }

    /**
     * Evaluate if any of the value changes would impact a condition. If so, send a CONDITION_CHECK back so that the UI can retry it
     * @param domChanges, potentially with added CONDITION_CHECK changes
     */
    private void evaluateConditionalChange(List<DOMChange> domChanges) {
        final Set<String> impactedDivIds = new HashSet<>();
        for(DOMChange domChange : domChanges) {
            if(domChange.getF() != null) {
                List<String> locallyImpactedIds = CONDITIONAL_REGISTRY.findByConditionField(domChange.getF());
                impactedDivIds.addAll(locallyImpactedIds);
            }
        }

        for(String impactedDivId : impactedDivIds) {
            DOMChange conditionCheck = new DOMChange(null, impactedDivId, DOMChange.DOMChangeType.CONDITION);
            conditionCheck.setC(CONDITIONAL_REGISTRY.get(impactedDivId));
            domChanges.add(conditionCheck);
        }
    }

    /**
     * Evaluate if any of the value changes would impact a condition. If so, send a CONDITION_CHECK back so that the UI can retry it
     * @param domChanges, potentially with added CONDITION_CHECK changes
     */
    private void evaluateConditionalClassChange(List<DOMChange> domChanges) {
        final Set<String> impactedDivIds = new HashSet<>();
        for(DOMChange domChange : domChanges) {
            if(domChange.getF() != null) {
                List<String> locallyImpactedIds = CONDITIONAL_CLASS_REGISTRY.findByConditionField(domChange.getF());
                impactedDivIds.addAll(locallyImpactedIds);
            }
        }

        for(String impactedDivId : impactedDivIds) {
            DOMChange conditionCheck = new DOMChange(null, impactedDivId, DOMChange.DOMChangeType.CONDITIONAL_CLASS);
            conditionCheck.setC(CONDITIONAL_CLASS_REGISTRY.get(impactedDivId));
            domChanges.add(conditionCheck);
        }
    }

    /**
     * Evaluate if any of the value changes would impact a generic m-attribute. If so, send an M-ATTR back so that the UI can retry it
     * @param domChanges, potentially with added M-ATTR changes
     */
    private void evaluateGenericMAttributesChanged(List<DOMChange> domChanges) {
        final Set<String> impactedDivIds = new HashSet<>();
        for(DOMChange domChange : domChanges) {
            if(domChange.getF() != null) {
                List<String> locallyImpactedIds = GenericMRegistry.getInstance().findByConditionField(domChange.getF());
                impactedDivIds.addAll(locallyImpactedIds);
            }
        }

        for(String impactedDivId : impactedDivIds) {
            GenericMRegistry.RegistryItem registryItem = GenericMRegistry.getInstance().get(impactedDivId);
            DOMChange conditionCheck = new DOMChange(registryItem.attribute.name(), impactedDivId, DOMChange.DOMChangeType.M_ATTR);
            conditionCheck.setC(registryItem.expression);
            domChanges.add(conditionCheck);
        }
    }

    /**
     * Evaluate if any of the value changes would impact an iteration. If so, send a ITERATION back so that the UI can retry it
     * @param domChanges, potentially with added ITERATION changes
     */
    private void evaluateIterationChange(List<DOMChange> domChanges) {
        Map<String, String> templatesToUpdate = new HashMap<>();
        for(DOMChange domChange : domChanges) {
            Set<String> relatedTemplates = IterationRegistry.getInstance().findRelatedToValue(domChange.getF());
            if(relatedTemplates != null) {
                for (String relatedTemplate : relatedTemplates) {
                    templatesToUpdate.put(relatedTemplate, domChange.getF());
                }
            }
        }

        domChanges.addAll(templatesToUpdate.entrySet().stream()
                .map(entry -> new DOMChange(entry.getKey(), entry.getValue(), DOMChange.DOMChangeType.ITERATION))
                .collect(Collectors.toList()));
    }

    /**
     * Evaluate if any of the changes would affect the title. If so, return a TITLE event so the UI can reparse it
     * @param session active websocket session
     * @param domChanges domChanges so far, can additional be appended with TITLE events
     */
    private void evaluateTitleChange(WebSocketSession session, List<DOMChange> domChanges) {
        String unmappedTitle = PageTitleRegistry.getInstance().getTitle(session);
        if(unmappedTitle != null) {
            boolean hasAChange = false;
            for(DOMChange domChange : domChanges) {
                String searchKey = "[$" + domChange.getF() + "]";
                if(unmappedTitle.contains(searchKey)) {
                    hasAChange = true;
                    unmappedTitle = unmappedTitle.replaceAll("\\[\\$"+domChange.getF()+"\\]", domChange.getV().toString());
                }
            }

            if(hasAChange) {
                domChanges.add(new DOMChange(null, unmappedTitle, DOMChange.DOMChangeType.TITLE));
            }
        }
    }

}
