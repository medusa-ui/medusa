package sample.getmedusa.showcase.samples.textinputs.formsubmit;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;
import java.util.Map;

@UIEventPage(path = "/detail/sample/form-submit", file = "/pages/sample/form-submit.html")
public class FormSubmitController {

    public List<Attribute> setupAttributes(){
        return List.of(new Attribute("result", ""), new Attribute("resultFromForm", ""));
    }

    public List<Attribute> displayName(Integer i, Map<String, Object> form, String s){
        return List.of(new Attribute("result", form.get("firstName") + " " + form.get("lastName")));
    }

    public List<Attribute> displayNameAsForm(Integer i, SampleForm form, String s){
        return List.of(new Attribute("resultFromForm", form.firstName() + " " + form.lastName()));
    }

    public record SampleForm(String firstName, String lastName) { }

}
