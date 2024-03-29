package io.getmedusa.medusa.core.validation;

import io.getmedusa.medusa.core.boot.ValidationDetection;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ValidationExecutorTest {

    private final ValidationExecutor v = ValidationExecutor.INSTANCE;

    @Test
    void testNotBlank() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(NotBlank.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation notBlankParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", notBlankParam, "xyz"));
        Assertions.assertNotNull(v.validateParam("", notBlankParam, ""));
        Assertions.assertNotNull(v.validateParam("", notBlankParam, "  "));
        Assertions.assertNotNull(v.validateParam("", notBlankParam, "    "));
    }

    @Test
    void testNotEmpty() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(NotEmpty.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation notEmptyParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", notEmptyParam, "xyz"));
        Assertions.assertNotNull(v.validateParam("", notEmptyParam, ""));
        Assertions.assertNull(v.validateParam("", notEmptyParam, "  "));
        Assertions.assertNull(v.validateParam("", notEmptyParam, "    "));
    }

    @Test
    void testPattern() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Pattern.class, "[a-zA-Z0-9]+", null, "x"));
        final ValidationDetection.ParamWithValidation patternParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", patternParam, "xYZ123"));
        Assertions.assertNotNull(v.validateParam("", patternParam, ""));
        Assertions.assertNotNull(v.validateParam("", patternParam, "  "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "    "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "~"));
    }

    @Test
    void testFalse() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(AssertFalse.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation patternParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", patternParam, "false"));
        Assertions.assertNull(v.validateParam("", patternParam, "FALSE"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "true"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "TRUE"));
        Assertions.assertNull(v.validateParam("", patternParam, "  "));
        Assertions.assertNull(v.validateParam("", patternParam, "    "));
        Assertions.assertNull(v.validateParam("", patternParam, "~"));
    }

    @Test
    void testTrue() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(AssertTrue.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation patternParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", patternParam, "true"));
        Assertions.assertNull(v.validateParam("", patternParam, "TRUE"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "false"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "FALSE"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "  "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "    "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "~"));
    }

    @Test
    void testDecimalMax() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(DecimalMax.class, "10.5", null, "x"));
        final ValidationDetection.ParamWithValidation patternParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", patternParam, "10.50"));
        Assertions.assertNull(v.validateParam("", patternParam, "8.456"));
        Assertions.assertNull(v.validateParam("", patternParam, "5"));
        Assertions.assertNull(v.validateParam("", patternParam, "-2.2435"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "12"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "FALSE"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "  "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "    "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "~"));
    }

    @Test
    void testDecimalMin() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(DecimalMin.class, "10.5", null, "x"));
        final ValidationDetection.ParamWithValidation patternParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", patternParam, "10.50"));
        Assertions.assertNull(v.validateParam("", patternParam, "18.456"));
        Assertions.assertNull(v.validateParam("", patternParam, "15"));
        Assertions.assertNull(v.validateParam("", patternParam, "22.2435"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "2"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "-2"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "FALSE"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "  "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "    "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "~"));
    }

    @Test
    void testEmail() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Email.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation patternParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", patternParam, "kdoe@email.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "j.ohn.doe@email.co.uk"));
        Assertions.assertNull(v.validateParam("", patternParam, "アシッシュ@ビジネス.コム"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "2"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "-2"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "@"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "  "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "    "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "~"));

        Assertions.assertNull(v.validateParam("", patternParam, "john.doe@gmail.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "k.e.v.i.n.doe@gmail.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "john.doe@gmail.co.uk"));
        Assertions.assertNull(v.validateParam("", patternParam, "john1235@gmail.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "john.doe@yahoo123.co.uk"));
        Assertions.assertNull(v.validateParam("", patternParam, "john_doe@yahoo.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "k_doe84@yahoo.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "johndoe_09@yahoo.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "kev1ndo3@icloud.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "i want ice cream@yahoo.com"));

        Assertions.assertNotNull(v.validateParam("", patternParam, "john.doegmail.com"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "johndoe@gmailcom"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "johndoe@gmail.com.."));
        Assertions.assertNotNull(v.validateParam("", patternParam, "gmail.co.uk"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "john7doe@gmail"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "john.doe@gmail.com\\"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "johndoe@accurate.org>"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "fasttimes123@johndoe@gmail.com"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "johndoe @gmailcom"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "johndoe@groupon.com,"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "john100000@yahoo.co,"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "johndoe@groupon.com,"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "john,doe84@yahoo.com"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "mailto:john_48@yahoo.com"));

        Assertions.assertNull(v.validateParam("", patternParam, "Abc@example.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "Abc.123@example.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "user+mailbox/department=shipping@example.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "!#$%&'*+-/=?^_`.{|}~@example.com"));

        Assertions.assertNull(v.validateParam("", patternParam, "用户@例子.广告"));
        Assertions.assertNull(v.validateParam("", patternParam, "अजय@डाटा.भारत"));
        Assertions.assertNull(v.validateParam("", patternParam, "квіточка@пошта.укр"));
        Assertions.assertNull(v.validateParam("", patternParam, "θσερ@εχαμπλε.ψομ"));
        Assertions.assertNull(v.validateParam("", patternParam, "Dörte@Sörensen.example.com"));
        Assertions.assertNull(v.validateParam("", patternParam, "коля@пример.рф"));
    }

    @Test
    void testDigits() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Digits.class, "2", "2", "x"));
        final ValidationDetection.ParamWithValidation patternParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", patternParam, "2"));
        Assertions.assertNull(v.validateParam("", patternParam, "3.23"));
        Assertions.assertNull(v.validateParam("", patternParam, "4.010"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "1.12345"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "-2.345345"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "8.331"));
        Assertions.assertNotNull(v.validateParam("", patternParam, "  "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "    "));
        Assertions.assertNotNull(v.validateParam("", patternParam, "~"));
    }

    @Test
    void testMax() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Max.class, "4.546", null, "x"));
        final ValidationDetection.ParamWithValidation maxParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", maxParam, "-2"));
        Assertions.assertNull(v.validateParam("", maxParam, "3"));
        Assertions.assertNull(v.validateParam("", maxParam, "4.546"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "6354"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "24234324"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "8"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "  "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "    "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "~"));
    }

    @Test
    void testMin() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Min.class, "4", null, "x"));
        final ValidationDetection.ParamWithValidation maxParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", maxParam, "6"));
        Assertions.assertNull(v.validateParam("", maxParam, "5"));
        Assertions.assertNull(v.validateParam("", maxParam, "4"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "1"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "-2"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "0"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "  "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "    "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "~"));
    }

    @Test
    void testPositive() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Positive.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation maxParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", maxParam, "1"));
        Assertions.assertNull(v.validateParam("", maxParam, "521134"));
        Assertions.assertNull(v.validateParam("", maxParam, "4.324"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "0"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "-2"));
        Assertions.assertNotNull(v.validateParam("", maxParam, ""));
        Assertions.assertNotNull(v.validateParam("", maxParam, "  "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "    "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "~"));
    }

    @Test
    void testPositiveOrZero() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(PositiveOrZero.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation maxParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", maxParam, "-0"));
        Assertions.assertNull(v.validateParam("", maxParam, "521134"));
        Assertions.assertNull(v.validateParam("", maxParam, "4.324"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "-1"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "-2"));
        Assertions.assertNotNull(v.validateParam("", maxParam, ""));
        Assertions.assertNotNull(v.validateParam("", maxParam, "  "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "    "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "~"));
    }

    @Test
    void testNegative() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Negative.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation maxParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", maxParam, "-1"));
        Assertions.assertNull(v.validateParam("", maxParam, "-521134"));
        Assertions.assertNull(v.validateParam("", maxParam, "-4.324"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "0"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "2"));
        Assertions.assertNotNull(v.validateParam("", maxParam, ""));
        Assertions.assertNotNull(v.validateParam("", maxParam, "  "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "    "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "~"));
    }

    @Test
    void testNegativeOrZero() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(NegativeOrZero.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation maxParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", maxParam, "-0"));
        Assertions.assertNull(v.validateParam("", maxParam, "-521134"));
        Assertions.assertNull(v.validateParam("", maxParam, "-4.324"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "1"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "2"));
        Assertions.assertNotNull(v.validateParam("", maxParam, ""));
        Assertions.assertNotNull(v.validateParam("", maxParam, "  "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "    "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "~"));
    }

    @Test
    void testNotNull() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(NotNull.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation maxParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", maxParam, "-0"));
        Assertions.assertNull(v.validateParam("", maxParam, "-521134"));
        Assertions.assertNull(v.validateParam("", maxParam, "-4.324"));
        Assertions.assertNull(v.validateParam("", maxParam, "1"));
        Assertions.assertNull(v.validateParam("", maxParam, ""));
        Assertions.assertNull(v.validateParam("", maxParam, "  "));
        Assertions.assertNull(v.validateParam("", maxParam, "    "));
        Assertions.assertNull(v.validateParam("", maxParam, "~"));
        Assertions.assertNotNull(v.validateParam("", maxParam, null));
    }

    @Test
    void testNull() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Null.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation maxParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNotNull(v.validateParam("", maxParam, "-0"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "-521134"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "-4.324"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "1"));
        Assertions.assertNotNull(v.validateParam("", maxParam, ""));
        Assertions.assertNotNull(v.validateParam("", maxParam, "  "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "    "));
        Assertions.assertNotNull(v.validateParam("", maxParam, "~"));
        Assertions.assertNull(v.validateParam("", maxParam, null));
    }

    @Test
    void testSize() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Size.class, null, "2", "x"));
        final ValidationDetection.ParamWithValidation maxParam = new ValidationDetection.ParamWithValidation("x", 0, validations);

        //string size
        Assertions.assertNull(v.validateParam("", maxParam, "ab"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "abcde"));

        //collection/array
        Assertions.assertNull(v.validateParam("", maxParam, "['a','b']"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "['a','b','c','d','e']"));

        //map
        Assertions.assertNull(v.validateParam("", maxParam, "{x: 'd', z: 3}"));
        Assertions.assertNotNull(v.validateParam("", maxParam, "{x: 'd',y: 't',r: 'w', z: 3}"));
    }

    @Test
    void testFuture() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Future.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation futureParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", futureParam, datePlusDays(1)));
        Assertions.assertNull(v.validateParam("", futureParam, datePlusDays(365)));
        Assertions.assertNotNull(v.validateParam("", futureParam, datePlusDays(-1)));
        Assertions.assertNotNull(v.validateParam("", futureParam, datePlusDays(0)));
        Assertions.assertNotNull(v.validateParam("", futureParam, "0"));
        Assertions.assertNotNull(v.validateParam("", futureParam, "xxx"));
        Assertions.assertNotNull(v.validateParam("", futureParam, "-4.324"));
        Assertions.assertNotNull(v.validateParam("", futureParam, "1"));
        Assertions.assertNotNull(v.validateParam("", futureParam, ""));
        Assertions.assertNotNull(v.validateParam("", futureParam, "  "));
        Assertions.assertNotNull(v.validateParam("", futureParam, "    "));
        Assertions.assertNotNull(v.validateParam("", futureParam, "~"));
    }

    @Test
    void testFutureOrPresent() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(FutureOrPresent.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation futureOrPresentParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", futureOrPresentParam, datePlusDays(1)));
        Assertions.assertNull(v.validateParam("", futureOrPresentParam, datePlusDays(365)));
        Assertions.assertNull(v.validateParam("", futureOrPresentParam, datePlusDays(0)));
        Assertions.assertNotNull(v.validateParam("", futureOrPresentParam, datePlusDays(-1)));
        Assertions.assertNotNull(v.validateParam("", futureOrPresentParam, "0"));
        Assertions.assertNotNull(v.validateParam("", futureOrPresentParam, "xxx"));
        Assertions.assertNotNull(v.validateParam("", futureOrPresentParam, "-4.324"));
        Assertions.assertNotNull(v.validateParam("", futureOrPresentParam, "1"));
        Assertions.assertNotNull(v.validateParam("", futureOrPresentParam, ""));
        Assertions.assertNotNull(v.validateParam("", futureOrPresentParam, "  "));
        Assertions.assertNotNull(v.validateParam("", futureOrPresentParam, "    "));
        Assertions.assertNotNull(v.validateParam("", futureOrPresentParam, "~"));
    }

    @Test
    void testPast() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(Past.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation pastParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", pastParam, datePlusDays(-1)));
        Assertions.assertNull(v.validateParam("", pastParam, datePlusDays(-365)));
        Assertions.assertNotNull(v.validateParam("", pastParam, datePlusDays(1)));
        Assertions.assertNotNull(v.validateParam("", pastParam, Long.toString(Long.MAX_VALUE)));
        Assertions.assertNotNull(v.validateParam("", pastParam, "xxx"));
        Assertions.assertNotNull(v.validateParam("", pastParam, "-4.324"));
        Assertions.assertNotNull(v.validateParam("", pastParam, ""));
        Assertions.assertNotNull(v.validateParam("", pastParam, "  "));
        Assertions.assertNotNull(v.validateParam("", pastParam, "    "));
        Assertions.assertNotNull(v.validateParam("", pastParam, "~"));
    }

    @Test
    void testPastOrPresent() {
        List<ValidationDetection.Validation> validations = List.of(new ValidationDetection.Validation(PastOrPresent.class, null, null, "x"));
        final ValidationDetection.ParamWithValidation pastOrPresentParam = new ValidationDetection.ParamWithValidation("x", 0, validations);
        Assertions.assertNull(v.validateParam("", pastOrPresentParam, datePlusDays(0)));
        Assertions.assertNull(v.validateParam("", pastOrPresentParam, datePlusDays(-1)));
        Assertions.assertNull(v.validateParam("", pastOrPresentParam, datePlusDays(-365)));
        Assertions.assertNotNull(v.validateParam("", pastOrPresentParam, datePlusDays(1)));
        Assertions.assertNotNull(v.validateParam("", pastOrPresentParam, Long.toString(Long.MAX_VALUE)));
        Assertions.assertNotNull(v.validateParam("", pastOrPresentParam, "xxx"));
        Assertions.assertNotNull(v.validateParam("", pastOrPresentParam, "-4.324"));
        Assertions.assertNotNull(v.validateParam("", pastOrPresentParam, ""));
        Assertions.assertNotNull(v.validateParam("", pastOrPresentParam, "  "));
        Assertions.assertNotNull(v.validateParam("", pastOrPresentParam, "    "));
        Assertions.assertNotNull(v.validateParam("", pastOrPresentParam, "~"));
    }

    private String datePlusDays(int days) {
        return Long.toString(System.currentTimeMillis() + (days*1000*60*60*24L));
    }
}
