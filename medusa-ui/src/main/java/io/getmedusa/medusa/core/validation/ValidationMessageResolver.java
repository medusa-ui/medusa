package io.getmedusa.medusa.core.validation;

import io.getmedusa.medusa.core.boot.ValidationDetection;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.List;

@Component
public class ValidationMessageResolver implements EmbeddedValueResolverAware {

    private StringValueResolver resolver;

    public String resolveMessage(ValidationError v) {
        return resolve(v.message(), v.field(), v.validation());
    }

    public List<ValidationDetection.FrontEndValidation> resolveMessages(List<ValidationDetection.FrontEndValidation> vList) {
        for(ValidationDetection.FrontEndValidation v : vList) {
            v.setMessage(resolve(v.getMessage(), v.getField(), v.getValidation()));
        }
        return vList;
    }

    private String resolve(String message, String field, String validation) {
        String newValue = message;
        if(newValue.startsWith("{")) {
            newValue = message.replace("{", "${");
        }

        try {
            newValue = resolver.resolveStringValue(newValue);
        } catch (IllegalArgumentException e) {
            return StandardEnglishValidationMessages.resolve(validation, field, message);
        }
        return newValue;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }
}
