package io.getmedusa.medusa.core.tags.action;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

@MedusaTag
public class MedusaUpload extends JSEventAttributeProcessor {

    //<input type="file" id="myFile" name="filename"> <br>
    //<button m:upload="myFile">upload file</button>

    public MedusaUpload() {
        super("upload", "onclick", "return _M.uploadFileToMethod(event, " + FRAGMENT_REPLACEMENT + ", '%s')");
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        attributeValue = replaceAttributeValues(context, attributeValue);
        attributeValue = replaceElementValues(attributeValue);
        structureHandler.setAttribute(eventName, eventTemplate.formatted(attributeValue));
    }
}