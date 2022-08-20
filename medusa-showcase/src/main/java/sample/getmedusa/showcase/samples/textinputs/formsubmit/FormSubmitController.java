package sample.getmedusa.showcase.samples.textinputs.formsubmit;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;

@UIEventPage(path = "/detail/sample/form-submit", file = "/pages/sample/form-submit.html")
public class FormSubmitController {

    public List<Attribute> setupAttributes(){
        return List.of(new Attribute("result", ""));
    }

    public List<Attribute> displayName(int i, SampleForm form, String s){
        return List.of(new Attribute("result", form.firstName + " " + form.lastName));
    }

    public record SampleForm(String firstName, String lastName) { }

}
