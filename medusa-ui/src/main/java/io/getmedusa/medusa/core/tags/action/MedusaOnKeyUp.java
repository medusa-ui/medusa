package io.getmedusa.medusa.core.tags.action;

import io.getmedusa.medusa.core.tags.annotation.MedusaTag;
import io.getmedusa.medusa.core.tags.meta.JSEventAttributeProcessor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import static io.getmedusa.medusa.core.tags.annotation.MedusaTag.prefix;

@MedusaTag
public class MedusaOnKeyUp extends JSEventAttributeProcessor {

    public static final String PRESSED_KEY_NAME = "key";

    public MedusaOnKeyUp() {
        super("keyup", "onkeyup", EVENT_TEMPLATE_M_DO_ACTION_ONKEYUP);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        String pressedKey = "'Enter'";
        if(tag.hasAttribute(prefix, PRESSED_KEY_NAME)) {
            IAttribute attribute = tag.getAttribute(prefix, PRESSED_KEY_NAME);
            pressedKey = attribute.getValue();
            if(!pressedKey.matches("[0-9]+")) { // test if keycode is used
                pressedKey = "'%s'".formatted(pressedKey);
            }
            structureHandler.removeAttribute(prefix, PRESSED_KEY_NAME);
        }
        attributeValue = replaceAttributeValues(context, attributeValue);
        attributeValue = replaceElementValues(attributeValue);
        structureHandler.setAttribute(eventName, eventTemplate.formatted(pressedKey, attributeValue));
    }

}