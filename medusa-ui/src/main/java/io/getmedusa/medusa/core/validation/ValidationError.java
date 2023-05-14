package io.getmedusa.medusa.core.validation;

import io.getmedusa.medusa.core.boot.ValidationDetection;

public record ValidationError(String formContext, String field, String validation, String message) {
    public ValidationError(String formContext, ValidationDetection.ParamWithValidation param, ValidationDetection.Validation validation) {
        this(formContext, param.field(), validation.type().getSimpleName(), validation.message());
    }
}