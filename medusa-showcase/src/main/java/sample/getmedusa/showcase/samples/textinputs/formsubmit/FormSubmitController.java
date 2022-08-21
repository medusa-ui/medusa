package sample.getmedusa.showcase.samples.textinputs.formsubmit;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.HashMap;
import java.util.List;

@UIEventPage(path = "/detail/sample/form-submit", file = "/pages/sample/form-submit.html")
public class FormSubmitController {

    public List<Attribute> setupAttributes(){
        return List.of(new Attribute("result", ""));
    }

    public List<Attribute> displayName(Integer i, HashMap form, String s){
        return List.of(new Attribute("result", form.get("firstName") + " " + form.get("lastName")));
    }

    public record SampleForm(String firstName, String lastName) { }

}
