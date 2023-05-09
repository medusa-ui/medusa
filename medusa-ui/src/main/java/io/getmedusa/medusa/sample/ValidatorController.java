package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@UIEventPage(path = "/validation", file = "/pages/validator.html")
public class ValidatorController {

    public List<Attribute> setupAttributes(){
        return Attribute.$$("result", "");
    }

    public List<Attribute> displayName(Integer i, @Valid SampleForm form, @Valid @NotBlank String s){
        return Attribute.$$("result", form.fullname());
    }

    public record SampleForm(@NotBlank String firstName, @Pattern(regexp = "[a-zA-Z0-9]+") @NotBlank String lastName) {
        public String fullname() {
            return firstName + " " + lastName;
        }
    }

}
