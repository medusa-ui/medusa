package io.getmedusa.medusa.core.injector;

import io.getmedusa.medusa.core.registry.ActiveDocument;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiffCheckService {

    public List<JSReadyDiff> diffCheckDocuments(ActiveDocument lastDocument, Document newDocument) {
        List<JSReadyDiff> diffs = new ArrayList<>();

        Diff differences = DiffBuilder
                .compare(cleanString(lastDocument.getDocument().outerHtml()))
                .withTest(cleanString(newDocument.outerHtml()))
                .build();

        for (Difference difference : differences.getDifferences()) {
            final Comparison comparison = difference.getComparison();
            if(!ComparisonType.CHILD_NODELIST_LENGTH.equals(comparison.getType())){
                final JSReadyDiff diff = comparisonToDiff(comparison);
                if(null != diff) { diffs.add(diff); }
            }
        }

        return diffs;
    }

    private JSReadyDiff comparisonToDiff(Comparison comparison) {
        final Node lastDocumentNode = comparison.getControlDetails().getTarget();
        final Node newDocumentNode = comparison.getTestDetails().getTarget();

        if(isAddition(lastDocumentNode, newDocumentNode)) {
            return createAdditionDiff(newDocumentNode, comparison.getTestDetails().getXPath());
        } else if(isRemoval(lastDocumentNode, newDocumentNode)) {
            return createRemovalDiff(comparison.getControlDetails().getXPath());
        } else if(comparison.getType().equals(ComparisonType.TEXT_VALUE)) {
            return createEditDiff(comparison.getTestDetails().getTarget().getTextContent(), comparison.getTestDetails().getParentXPath());
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
        JSReadyDiff d = new JSReadyDiff(DiffType.REMOVAL);
        d.setXpath(xPath);
        return d;
    }

    private JSReadyDiff createEditDiff(String textContent, String xPath) {
        JSReadyDiff d = new JSReadyDiff(DiffType.EDIT);
        d.setXpath(xPath);
        d.setContent(textContent);
        return d;
    }

    private JSReadyDiff createAdditionDiff(Node node, String xPath) {
        JSReadyDiff d = new JSReadyDiff(DiffType.ADDITION);
        d.setXpath(xPath);
        d.setContent(nodeToContent(node));
        return d;
    }

    private String nodeToContent(Node node) {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(node);
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            return result.getWriter().toString().substring(38);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private String cleanString(String str) {
        return str.trim()
                .replace("\n", "")
                .replace(">   <", "><")
                .replace(">  <", "><")
                .replace("> <", "><");
    }

    private Set<String> getAllHashes(Document document, String attribute) {
        Set<String> hashes = new HashSet<>();
        for(Element e : document.getAllElements()) {
            hashes.add(e.attr(attribute));
        }
        return hashes;
    }
}

