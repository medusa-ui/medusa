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

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static io.getmedusa.medusa.core.router.action.JSReadyDiff.*;
import static org.xmlunit.diff.ComparisonType.*;

@Component
public class DiffEngine {

    private static final TransformerFactory T_FACTORY = TransformerFactory.newInstance();
    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final int XML_VERSION_LENGTH = XML_VERSION.length();

    public Flux<List<JSReadyDiff>> findDiffs(String oldHTML, String newHTML) {
        List<JSReadyDiff> diffs = new ArrayList<>();

        try {
            Diff differences = DiffBuilder
                    .compare(oldHTML)
                    .withTest(newHTML)
                    .ignoreComments()
                    .ignoreWhitespace()
                    .build();

            //TODO in a list, what does it mean to remove elements and also add elements?
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

        return Flux.just(diffs);
    }

    private JSReadyDiff comparisonToDiff(Comparison comparison) {
        final Comparison.Detail oldDetail = comparison.getControlDetails();
        final Comparison.Detail newDetail = comparison.getTestDetails();

        final Node lastDocumentNode = oldDetail.getTarget();
        final Node newDocumentNode = newDetail.getTarget();

        if(ELEMENT_TAG_NAME.equals(comparison.getType())) {
            //return buildNewEdit(oldDetail.getXPath(), nodeToContent(newDocumentNode));
            return null; //TODO might add? but not right now
        } else if(ATTR_NAME_LOOKUP.equals(comparison.getType())){
            //return buildNewEdit(oldDetail.getParentXPath(), nodeToContent(newDocumentNode));
            return null; //TODO should do this, but not right now
        } else if(isAddition(comparison)) {
            return buildNewAddition(newDetail.getXPath(), nodeToContent(newDocumentNode));
        } else if(isRemoval(comparison)) {
            return buildNewRemoval(oldDetail.getXPath());
        } else if(TEXT_VALUE.equals(comparison.getType())) {
            return buildNewEdit(newDetail.getParentXPath(), nodeToContent(newDocumentNode.getParentNode()));
        } else {
            System.out.println();
        }

        return null;
    }

    private boolean isAddition(Comparison comparison) {
        final Comparison.Detail oldDetail = comparison.getControlDetails();
        final Comparison.Detail newDetail = comparison.getTestDetails();

        final boolean isAddition = CHILD_LOOKUP.equals(comparison.getType()) && oldDetail.getTarget() == null && newDetail.getTarget() != null;
        return isAddition && matchIfNotEmptyText(newDetail); //don't send meaningless diffs of empty text additions
    }

    private boolean isRemoval(Comparison comparison) {
        final Comparison.Detail oldDetail = comparison.getControlDetails();
        final Comparison.Detail newDetail = comparison.getTestDetails();

        final boolean isRemoval = CHILD_LOOKUP.equals(comparison.getType()) && oldDetail.getTarget() != null && newDetail.getTarget() == null;
        return isRemoval && matchIfNotEmptyText(oldDetail); //don't send meaningless diffs of empty text additions
    }

    private boolean isEmptyText(Comparison.Detail detail, String nodeToContent) {
        return detail.getValue().toString().equals("#text") && nodeToContent.trim().isBlank();
    }

    private boolean matchIfNotEmptyText(Comparison.Detail oldDetail) {
        final String nodeToContent = nodeToContent(oldDetail.getTarget());
        return nodeToContent != null && !isEmptyText(oldDetail, nodeToContent);
    }

    private String nodeToContent(Node node) {
        try {
            DOMSource source = new DOMSource(node);
            StreamResult result = new StreamResult(new StringWriter());
            T_FACTORY.newTransformer().transform(source, result);
            final String s = result.getWriter().toString();
            if(s.startsWith(XML_VERSION)) {
                return s.substring(XML_VERSION_LENGTH);
            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
