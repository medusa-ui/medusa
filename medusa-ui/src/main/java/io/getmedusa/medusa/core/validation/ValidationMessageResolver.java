package io.getmedusa.medusa.core.validation;

import io.getmedusa.medusa.core.boot.ValidationDetection;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.List;
import java.util.Locale;

@Component
public class ValidationMessageResolver implements EmbeddedValueResolverAware {

    private final MessageSource messageSource;

    public ValidationMessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private StringValueResolver resolver;

    public String resolveMessage(ValidationError v, Locale locale) {
        return resolve(v.message(), v.field(), v.validation(), locale);
    }

    public List<ValidationDetection.FrontEndValidation> resolveMessages(List<ValidationDetection.FrontEndValidation> vList) {
        for(ValidationDetection.FrontEndValidation v : vList) {
            v.setMessage(resolve(v.getMessage(), v.getField(), v.getValidation(), Locale.getDefault()));
        }
        return vList;
    }

    private String resolve(String message, String field, String validation, Locale locale) {
        String newValue = message;
        try {
            if(newValue.startsWith("{")) {
                newValue = messageSource.getMessage(message.substring(1, message.length()-1), new String[]{field}, locale);
            }
            newValue = resolver.resolveStringValue(newValue);
        } catch (IllegalArgumentException | NoSuchMessageException e) {
            return StandardEnglishValidationMessages.resolve(validation, field, message);
        }
        return newValue;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }
}
