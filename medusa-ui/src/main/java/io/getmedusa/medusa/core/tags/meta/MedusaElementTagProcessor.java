package io.getmedusa.medusa.core.tags.meta;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;

import java.util.List;

import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.*;

/**
 * Medusa Tag Processor
 */
public abstract class MedusaElementTagProcessor extends AbstractElementModelProcessor {

    public static final int PRECEDENCE = 1000;

    /**
     * Process tag by elementName
     *
     * @param elementName the element to process
     */
    public MedusaElementTagProcessor(String elementName) {
        super(
                templateMode,
                prefix,
                elementName,
                true,
                null,
                false,
                PRECEDENCE);
    }

    /**
     * Retrieve the child Elements form the given model
     *
     * @param model the model
     * @return list of Elements
     */
    protected List<Element>  getNodeList(IModel model) {
        Document document = Jsoup.parse(model.toString());
        return document.body().getAllElements();
    }
}
