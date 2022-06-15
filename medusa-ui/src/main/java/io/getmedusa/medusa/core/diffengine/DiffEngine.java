package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import reactor.core.publisher.Flux;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Component
public class DiffEngine {

    public Flux<JSReadyDiff> findDiffs(String oldHTML, String newHTML) {
        List<JSReadyDiff> diffs = new ArrayList<>();

        try {
            Diff differences = DiffBuilder
                    .compare(oldHTML)
                    .withTest(newHTML)
                    .build();

            for (Difference difference : differences.getDifferences()) {
                final Comparison comparison = difference.getComparison();
                if (!ComparisonType.CHILD_NODELIST_LENGTH.equals(comparison.getType())) {
                    final JSReadyDiff diff = comparisonToDiff(comparison);
                    if (null != diff) {
                        diffs.add(diff);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return Flux.fromIterable(diffs);
    }

    private JSReadyDiff comparisonToDiff(Comparison comparison) {
        final Node lastDocumentNode = comparison.getControlDetails().getTarget();
        final Node newDocumentNode = comparison.getTestDetails().getTarget();

        if(isAddition(lastDocumentNode, newDocumentNode)) {
            return createAdditionDiff(newDocumentNode, comparison.getTestDetails().getXPath());
        } else if(isRemoval(lastDocumentNode, newDocumentNode)) {
            return createRemovalDiff(comparison.getControlDetails().getXPath());
        } else if(comparison.getType().equals(ComparisonType.TEXT_VALUE)) {
            return createEditDiff(comparison.getTestDetails().getTarget().getParentNode(), comparison.getTestDetails().getParentXPath());
        }

        return null;
    }

    private boolean isAddition(Node lastDocumentNode, Node newDocumentNode) {
        return lastDocumentNode == null && newDocumentNode != null;
    }

    private boolean isRemoval(Node lastDocumentNode, Node newDocumentNode) {
        return lastDocumentNode != null && newDocumentNode == null;
    }

    private JSReadyDiff createRemovalDiff(String xPath) {
        return JSReadyDiff.buildNewRemoval(xPath);
    }

    private JSReadyDiff createEditDiff(Node node, String xPath) {
        return JSReadyDiff.buildNewEdit(xPath, nodeToContent(node));
    }

    private JSReadyDiff createAdditionDiff(Node node, String xPath) {
        return JSReadyDiff.buildNewAddition(xPath, nodeToContent(node));
    }

    private String nodeToContent(Node node) {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(node);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            return result.getWriter().toString().substring(38); //TODO magic number
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
