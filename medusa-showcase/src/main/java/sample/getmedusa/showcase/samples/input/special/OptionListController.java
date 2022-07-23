package sample.getmedusa.showcase.samples.input.special;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import sample.getmedusa.showcase.samples.AbstractSampleController;

import java.util.List;

@UIEventPage(path = "/detail/sample/option-list", file = "/pages/sample/option-list.html")
public class OptionListController extends AbstractSampleController {

        public List<Attribute> setupAttributes(){
        return List.of(new Attribute("fruits", List.of("Apple", "Banana", "Lemon", "Orange", "Strawberry" )));
    }

    public List<Attribute> favorite(String fruit) {
        return List.of(new Attribute("favorite", fruit));
    }
}
