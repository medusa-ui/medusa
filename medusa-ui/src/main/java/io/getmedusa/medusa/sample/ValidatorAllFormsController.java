package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@UIEventPage(path = "/validation-all", file = "/pages/validator-all.html")
public class ValidatorAllFormsController {

    public List<Attribute> setupAttributes(){
        return Attribute.$$("result", "No result yet", "form", "No form run yet");
    }

    public List<Attribute> doNoValidationForm(EmailForm form){
        return Attribute.$$("result", form.value(), "form", "NoValidationForm");
    }

    public List<Attribute> doAssertFalseForm(@Valid AssertFalseForm form){
        return Attribute.$$("result", form.value(), "form", "AssertFalseForm");
    }

    public List<Attribute> doAssertTrueForm(@Valid AssertTrueForm form){
        return Attribute.$$("result", form.value(), "form", "AssertTrueForm");
    }

    public List<Attribute> doDecimalMaxForm(@Valid DecimalMaxForm form){
        return Attribute.$$("result", form.value(), "form", "DecimalMaxForm");
    }
    public List<Attribute> doDecimalMinForm(@Valid DecimalMinForm form){
        return Attribute.$$("result", form.value(), "form", "DecimalMinForm");
    }

    public List<Attribute> doDigitsForm(@Valid DigitsForm form){
        return Attribute.$$("result", form.value(), "form", "DigitsForm");
    }

    public List<Attribute> doDigits2Form(@Valid DigitsForm2 form){
        return Attribute.$$("result", form.value(), "form", "Digits2Form");
    }

    public List<Attribute> doEmailForm(@Valid EmailForm form){
        return Attribute.$$("result", form.value(), "form", "EmailForm");
    }

    public List<Attribute> doMaxForm(@Valid MaxForm form){
        return Attribute.$$("result", form.value(), "form", "MaxForm");
    }

    public List<Attribute> doMinForm(@Valid MinForm form){
        return Attribute.$$("result", form.value().toString(), "form", "MinForm");
    }

    public List<Attribute> doNegativeForm(@Valid NegativeForm form){
        return Attribute.$$("result", Integer.toString(form.value()), "form", "NegativeForm");
    }

    public List<Attribute> doNegativeOrZeroForm(@Valid NegativeOrZeroForm form){
        return Attribute.$$("result", Long.toString(form.value()), "form", "NegativeOrZeroForm");
    }

    public List<Attribute> doPositiveForm(@Valid PositiveForm form){
        return Attribute.$$("result", Double.toString(form.value()), "form", "PositiveForm");
    }

    public List<Attribute> doPositiveOrZeroForm(@Valid PositiveOrZeroForm form){
        return Attribute.$$("result", Short.toString(form.value()), "form", "PositiveOrZeroForm");
    }

    public List<Attribute> doNotBlankForm(@Valid NotBlankForm form){
        return Attribute.$$("result", form.value(), "form", "NotBlankForm");
    }

    public List<Attribute> doNotEmptyForm(@Valid NotEmptyForm form){
        return Attribute.$$("result", form.value(), "form", "NotEmptyForm");
    }

    public List<Attribute> doNotNullForm(@Valid NotNullForm form){
        return Attribute.$$("result", form.value(), "form", "NotNullForm");
    }

    public List<Attribute> doNullForm(@Valid NullForm form){
        return Attribute.$$("result", form.value(), "form", "NullForm");
    }

    public List<Attribute> doFutureForm(@Valid FutureForm form){
        return Attribute.$$("result", form.value(), "form", "FutureForm");
    }

    public List<Attribute> doFutureOrPresentForm(@Valid FutureOrPresentForm form){
        return Attribute.$$("result", form.value(), "form", "FutureOrPresentForm");
    }
    public List<Attribute> doPastForm(@Valid PastForm form){
        return Attribute.$$("result", form.value(), "form", "PastForm");
    }

    public List<Attribute> doPastOrPresentForm(@Valid PastOrPresentForm form){
        return Attribute.$$("result", form.value(), "form", "PastOrPresentForm");
    }

    public List<Attribute> doPatternForm(@Valid PatternForm form){
        return Attribute.$$("result", form.value(), "form", "PatternForm");
    }
    
    public List<Attribute> doSizeStringForm(@Valid SizeStringForm form){
        return Attribute.$$("result", form.value(), "form", "SizeStringForm");
    }

    public List<Attribute> doSizeArrayForm(@Valid SizeArrayForm form){
        return Attribute.$$("result", Arrays.toString(form.value()), "form", "SizeArrayForm");
    }

    public List<Attribute> doSizeMapForm(@Valid SizeMapForm form){
        return Attribute.$$("result", form.value.toString(), "form", "SizeMapForm");
    }

    public record AssertFalseForm(@AssertFalse boolean value) { }

    public record AssertTrueForm(@AssertTrue boolean value) { }

    public record DecimalMaxForm(@DecimalMax(value = "10", inclusive = false) Double value) { }

    public record DecimalMinForm(@DecimalMin(value = "2", inclusive = false) Double value) { }

    public record DigitsForm(@Digits(integer = 1, fraction = 1) BigDecimal value) { }

    public record DigitsForm2(@Digits(integer = 2, fraction = 0) Double value) { }

    public record EmailForm(@Email String value) { }

    public record MaxForm(@Max(value = 123) Double value) { }

    public record MinForm(@Min(value = 300) BigInteger value) { }

    public record NegativeForm(@Negative int value) { }

    public record NegativeOrZeroForm(@NegativeOrZero long value) { }

    public record PositiveForm(@Positive double value) { }

    public record PositiveOrZeroForm(@PositiveOrZero Short value) { }

    public record NotBlankForm(@NotBlank String value) { }

    public record NotEmptyForm(@NotEmpty String value) { }

    public record NotNullForm(@NotNull String value) { }

    public record NullForm(@Null String value) { }

    public record FutureForm(@Future LocalDate value) { }

    public record FutureOrPresentForm(@FutureOrPresent LocalDateTime value) { }
    public record PastForm(@Past Date value) { }

    public record PastOrPresentForm(@PastOrPresent LocalDate value) { }

    public record PatternForm(@Pattern(regexp = "b[aeiou]bble") String value) { }

    public record SizeStringForm(@Size(max = 12) String value) { }

    public record SizeArrayForm(@Size(max = 2) String[] value) { }

    public record SizeMapForm(@Size(max = 2) Map<String, String> value) { }

}
