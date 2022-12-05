package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.joox.JOOX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

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
    private static final Logger logger = LoggerFactory.getLogger(DiffEngine.class);
    private static final TransformerFactory T_FACTORY = TransformerFactory.newInstance();
    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final int XML_VERSION_LENGTH = XML_VERSION.length();
    private static final DiffComparator DIFF_COMPARATOR = new DiffComparator();

    public List<JSReadyDiff> findDiffs(String oldHTML, String newHTML) {
        List<JSReadyDiff> diffs = new ArrayList<>();

        try {
            Diff differences = DiffBuilder
                    .compare(oldHTML)
                    .withTest(newHTML)
                    .ignoreComments()
                    .ignoreWhitespace()
                    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                    .build();

            for (Difference difference : differences.getDifferences()) {
                final Comparison comparison = difference.getComparison();

                logger.info("difference: {}", comparison);

                if (!isOfIgnorableType(comparison.getType())) {
                    final JSReadyDiff diff = comparisonToDiff(comparison);
                    if (null != diff) {
                        diffs.add(diff);
                    }
                } else {
                    logger.error("ignored: {}", difference);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        diffs.sort(DIFF_COMPARATOR);

        return diffs;
    }

    private boolean isOfIgnorableType(ComparisonType type) {
        return CHILD_NODELIST_LENGTH.equals(type) ||
               ELEMENT_NUM_ATTRIBUTES.equals(type);
    }

    private JSReadyDiff comparisonToDiff(Comparison comparison) {
        final Comparison.Detail oldDetail = comparison.getControlDetails();
        final Comparison.Detail newDetail = comparison.getTestDetails();

        final Node newDocumentNode = newDetail.getTarget();

        if(ELEMENT_TAG_NAME.equals(comparison.getType())) {
            //return buildNewEdit(oldDetail.getXPath(), nodeToContent(newDocumentNode));
            logger.debug("comparisonToDiff: no implementation for comparison: " + comparison + " of type ELEMENT_TAG_NAME");
            return null;
        } else if(ATTR_NAME_LOOKUP.equals(comparison.getType())){
            return buildNewEdit(attrOwnerXPath(oldDetail, newDetail), nodeToContent(newDocumentNode));
        } else if(isAddition(comparison)) {
            return buildNewAddition(newDetail.getXPath(), nodeToContent(newDocumentNode));
        } else if(isRemoval(comparison)) {
            return buildNewRemoval(oldDetail.getXPath());
        } else if(TEXT_VALUE.equals(comparison.getType())) {
            return buildNewEdit(newDetail.getParentXPath(), nodeToContent(newDocumentNode.getParentNode()));
        } else if(ATTR_VALUE.equals(comparison.getType())) {
            return buildAttrChange(attrOwnerXPath(oldDetail, newDetail), newDocumentNode.getNodeName(), newDocumentNode.getNodeValue());
        } else if(CHILD_NODELIST_SEQUENCE.equals(comparison.getType())){
            int index = Integer.parseInt(newDetail.getValue().toString());
            //we should be able to do this, because we sort so that all additions are already done
            //any new elements should already exist
            var addBeforeNode = newDocumentNode.getParentNode().getChildNodes().item(index + 1);
            if(null == addBeforeNode) {
                //add as last
                return buildSequenceChange(oldDetail.getXPath(), "::LAST");
            } else {
                //to xpath
                String path = JOOX.$(addBeforeNode).xpath();
                return buildSequenceChange(oldDetail.getXPath(), path);
            }
        } else {
            logger.warn("comparisonToDiff: no match for comparison: " + comparison);
        }

        return null;
    }

    private String attrOwnerXPath(Comparison.Detail oldDetail, Comparison.Detail newDetail) {
        String attrXPath = newDetail.getParentXPath();
        if(oldDetail.getXPath().length() > newDetail.getXPath().length()) {
            attrXPath = oldDetail.getParentXPath();
        }
        return attrXPath;
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
        return "#text".equals(detail.getValue().toString()) && nodeToContent.trim().isBlank();
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
