package sample.getmedusa.showcase.samples.textinputs.references;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;

import static io.getmedusa.medusa.core.attributes.Attribute.$$;

@UIEventPage(path = "/detail/sample/submit-for-reference", file = "/pages/sample/submit-for-reference.html")
public class SubmitWithReferenceController {

    public List<Attribute> setupAttributes(){
        return $$("result", "");
    }

    public List<Attribute> display(SampleForm form, String myInput, String myParagraph){
        return $$("result",
                form.firstName() + " " + form.lastName()
                + " || " + myInput + " || " + myParagraph);
    }

    public record SampleForm(String firstName, String lastName) { }

}
