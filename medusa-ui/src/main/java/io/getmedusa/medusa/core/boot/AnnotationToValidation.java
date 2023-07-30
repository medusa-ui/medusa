package io.getmedusa.medusa.core.boot;

import io.getmedusa.medusa.core.annotation.MaxFileSize;
import io.getmedusa.medusa.core.validation.ValidationMessageResolver;
import jakarta.validation.constraints.*;
import org.springframework.util.unit.DataSize;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

public class AnnotationToValidation {

    private AnnotationToValidation() {}

    private static ValidationMessageResolver resolver;

    public static List<ValidationDetection.Validation> findValidations(AnnotatedElement field, ValidationMessageResolver resolver) {
        AnnotationToValidation.resolver = resolver;
        List<ValidationDetection.Validation> validations = new ArrayList<>();

        assertFalse(validations, field);
        assertTrue(validations, field);
        decimalMax(validations, field);
        decimalMin(validations, field);
        digits(validations, field);
        email(validations, field);
        future(validations, field);
        futureOrPresent(validations, field);
        max(validations, field);
        min(validations, field);
        negative(validations, field);
        negativeOrZero(validations, field);
        notBlank(validations, field);
        notEmpty(validations, field);
        notNull(validations, field);
        isNull(validations, field);
        past(validations, field);
        pastOrPresent(validations, field);
        pattern(validations, field);
        positive(validations, field);
        positiveOrZero(validations, field);
        size(validations, field);
        maxFileSize(validations, field);
        return validations;
    }

    private static void maxFileSize(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        MaxFileSize annotation = field.getAnnotation(MaxFileSize.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(MaxFileSize.class, Long.toString(DataSize.parse(valueInterpreter(annotation.value())).toBytes()),null, annotation.message()));
        }
    }

    private static void notBlank(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        NotBlank annotation = field.getAnnotation(NotBlank.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(NotBlank.class, null,null, annotation.message()));
        }
    }


    private static void assertFalse(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        AssertFalse annotation = field.getAnnotation(AssertFalse.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(AssertFalse.class, null,null, annotation.message()));
        }
    }

    private static void assertTrue(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        AssertTrue annotation = field.getAnnotation(AssertTrue.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(AssertTrue.class, null,null, annotation.message()));
        }
    }

    private static void decimalMax(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        DecimalMax annotation = field.getAnnotation(DecimalMax.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(DecimalMax.class, valueInterpreter(annotation.value()),null, annotation.message()));
        }
    }
    private static void decimalMin(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        DecimalMin annotation = field.getAnnotation(DecimalMin.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(DecimalMin.class, valueInterpreter(annotation.value()),null, annotation.message()));
        }
    }

    private static void digits(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Digits annotation = field.getAnnotation(Digits.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Digits.class, Integer.toString(annotation.integer()),Integer.toString(annotation.fraction()), annotation.message()));
        }
    }

    private static void email(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Email annotation = field.getAnnotation(Email.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Email.class, valueInterpreter(annotation.regexp()),null, annotation.message()));
        }
    }

    private static void future(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Future annotation = field.getAnnotation(Future.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Future.class, null,null, annotation.message()));
        }
    }

    private static void futureOrPresent(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        FutureOrPresent annotation = field.getAnnotation(FutureOrPresent.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(FutureOrPresent.class, null,null, annotation.message()));
        }
    }

    private static void max(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Max annotation = field.getAnnotation(Max.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Max.class, Long.toString(annotation.value()),null, annotation.message()));
        }
    }

    private static void min(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Min annotation = field.getAnnotation(Min.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Min.class, Long.toString(annotation.value()),null, annotation.message()));
        }
    }

    private static void negative(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Negative annotation = field.getAnnotation(Negative.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Negative.class, null,null, annotation.message()));
        }
    }
    private static void negativeOrZero(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        NegativeOrZero annotation = field.getAnnotation(NegativeOrZero.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(NegativeOrZero.class, null,null, annotation.message()));
        }
    }

    private static void notEmpty(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        NotEmpty annotation = field.getAnnotation(NotEmpty.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(NotEmpty.class, null,null, annotation.message()));
        }
    }

    private static void notNull(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        NotNull annotation = field.getAnnotation(NotNull.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(NotNull.class, null,null, annotation.message()));
        }
    }

    private static void isNull(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Null annotation = field.getAnnotation(Null.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Null.class, null,null, annotation.message()));
        }
    }

    private static void past(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Past annotation = field.getAnnotation(Past.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Past.class, null,null, annotation.message()));
        }
    }

    private static void pastOrPresent(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        PastOrPresent annotation = field.getAnnotation(PastOrPresent.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(PastOrPresent.class, null,null, annotation.message()));
        }
    }

    private static void pattern(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Pattern annotation = field.getAnnotation(Pattern.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Pattern.class, valueInterpreter(annotation.regexp()),null, annotation.message()));
        }
    }

    private static void positive(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Positive annotation = field.getAnnotation(Positive.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Positive.class, null,null, annotation.message()));
        }
    }

    private static void positiveOrZero(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        PositiveOrZero annotation = field.getAnnotation(PositiveOrZero.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(PositiveOrZero.class, null,null, annotation.message()));
        }
    }
    private static void size(List<ValidationDetection.Validation> validations, AnnotatedElement field) {
        Size annotation = field.getAnnotation(Size.class);
        if(null != annotation) {
            validations.add(new ValidationDetection.Validation(Size.class, Integer.toString(annotation.min()), Integer.toString(annotation.max()), annotation.message()));
        }
    }

    private static String valueInterpreter(String rawValue) {
        return resolver.resolveValue(rawValue);
    }
}
