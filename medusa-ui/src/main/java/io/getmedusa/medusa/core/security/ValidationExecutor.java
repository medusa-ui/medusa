package io.getmedusa.medusa.core.security;

import io.getmedusa.medusa.core.boot.ValidationDetection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum ValidationExecutor {

    INSTANCE;

    public Set<ValidationError> validate(String expression, Object bean) {
        Set<ValidationError> violationSet = new HashSet<>();
        if(ValidationDetection.INSTANCE.doesBeanHaveMethodsWithValidation(bean)) {
            String[] split = expression.replace(")", "").split("\\(");
            final String method = split[0];

            if(ValidationDetection.INSTANCE.doesMethodHaveValidationParameters(bean, method)) {
                final List<String> parameters = List.of(split[1].split(",(?![^{}]*})"));
                violationSet.addAll(ValidationDetection.INSTANCE.findValidations(bean, method, parameters));
            }
        }

        return violationSet;
    }

    //TODO I'm not sure if its best to make an implementation ourselves or use something like Hibernate Validator
    // you might think: no need to re-implement; but actually we need this information anyway for the front-end
    // so the effort to have manual validation is kind of minimal compared to all the up-front work, and we save a dependency?
    // (basically just this method)
    public ValidationError validateParam(ValidationDetection.ParamWithValidation param, String valueToValidate) {
        for(ValidationDetection.Validation validation : param.validations()) {
            if (NotBlank.class.equals(validation.type()) && validateNotBlank(valueToValidate)) {
                return new ValidationError(param.field(), validation.message());
            } else if(Pattern.class.equals(validation.type()) && validatePattern(validation.value(), valueToValidate)) {
                return new ValidationError(param.field(), validation.message());
            } else {
                //TODO
            }
        }
        return null;
    }

    private boolean validatePattern(String pattern, String valueToValidate) {
        return valueToValidate == null || valueToValidate.replaceFirst(pattern, "-").equals("-");
    }

    private boolean validateNotBlank(String valueToValidate) {
        return valueToValidate == null || valueToValidate.isBlank();
    }
}
