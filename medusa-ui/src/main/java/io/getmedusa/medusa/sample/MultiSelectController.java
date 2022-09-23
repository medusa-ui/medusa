package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@UIEventPage(path = "/multi-select", file = "/pages/multi-select")
public class MultiSelectController {

    private static final Logger logger = LoggerFactory.getLogger(MultiSelectController.class);

    public List<Attribute> setupAttributes() {
        return List.of(
                new Attribute("favoriteFruits", List.of("Orange","Banana")),
                new Attribute("fruits", List.of("Apple", "Banana", "Lemon", "Orange", "Strawberry"))
        );
    }

    /* use map */
    public List<Attribute> favoritesMap(Map<String, Object> form) {
        logger.info("favorite fruits: " + form.get("favoriteFruits"));  /* single selection != is not a list with one option */
        return List.of(
                new Attribute("favoriteFruits", List.of("Banana"))
        );
    }

    /* use FormObject */
    public List<Attribute> favoritesFormObject(FormObject form) {
        logger.info("With formObject: " + form.favoriteFruits()); /* ERROR when a single option was selected */
        return List.of(
                new Attribute("favoriteFruits", List.of("Banana"))
        );
    }

    public record FormObject(List<String> favoriteFruits){}

}
