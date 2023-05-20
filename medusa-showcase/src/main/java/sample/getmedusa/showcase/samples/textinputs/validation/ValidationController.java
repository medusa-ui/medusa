package sample.getmedusa.showcase.samples.textinputs.validation;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@UIEventPage(path = "/detail/sample/validation", file = "/pages/sample/validation.html")
public class ValidationController {

    public List<Attribute> setupAttributes(){
        return Attribute.$$("result", "",
                "result_customMessage", "");
    }

    public List<Attribute> validateMyForm(@Valid SampleForm form){
        return List.of(new Attribute("result", form.email() + " " + form.yearOfBirth()));
    }

    public List<Attribute> validateMyFormWithACustomMessage(@Valid @Email(message = "Gelieve een geldig e-mail in te vullen") String email2){
        return List.of(new Attribute("result_customMessage", email2.split("@")[0]));
    }

    public record SampleForm(@Email String email, @NotBlank @Pattern(regexp = "^(19\\d{2}|2\\d{3}|3000)$") Integer yearOfBirth) { }

}
