package sample.getmedusa.showcase.samples.input.special;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;
import java.util.Map;

@UIEventPage(path = "/detail/sample/multi-select", file = "/pages/sample/multi-select.html")
public class MultiSelectController {

    List<String> fruits= List.of("Apple", "Banana", "Lemon", "Orange", "Strawberry" );

    public List<Attribute> setupAttributes(){
        return List.of(
                new Attribute("fruits", fruits)
        );
    }

    /* Using a Map as form */
    public List<Attribute> favoriteFruits(Map<String, Object> form) {
        if(null == form) return List.of();
        return List.of(new Attribute("favoritesMap", form.get("favorites")));
    }

    /* Using a FormObject */
    public List<Attribute> favorites(FormObject formObject) {
        if(null == formObject) return List.of();
        return List.of(new Attribute("favoritesForm", formObject.favoriteFruits));
    }

    public record FormObject(List<String> favoriteFruits){}
}
