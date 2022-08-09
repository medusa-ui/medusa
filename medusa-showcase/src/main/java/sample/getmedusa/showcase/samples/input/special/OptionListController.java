package sample.getmedusa.showcase.samples.input.special;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UIEventPage(path = "/detail/sample/option-list", file = "/pages/sample/option-list.html")
public class OptionListController {

        private final Map<String, List<String>> drinks =
                Map.of("Waters", List.of(" ", "Plain water", "Sparkling water", "Flavored water"),
                       "Hot Drinks", List.of(" ", "Coffee", "Thee", "Hot chocolate"),
                       "Beers", List.of(" ","Dark ale", "IPA"));

        public List<Attribute> setupAttributes(){
        return List.of(
                new Attribute("favorite", "Orange"),
                new Attribute("fruits", List.of("","Apple", "Banana", "Lemon", "Orange", "Strawberry" )),

                new Attribute("order", new ArrayList<String>()),
                new Attribute("drinksType", drinks.keySet()),
                new Attribute( "drinksList" ,"select type")
        );
    }

    public List<Attribute> favorite(String fruit) {
        return List.of(new Attribute("favorite", fruit));
    }

    public List<Attribute> drinks(String type) {
        return List.of(new Attribute("drinksList", drinks.get(type)));
    }

    public List<Attribute> order(Session session, String drink) {
        List<String> order = session.getAttribute("order");
        order.add(drink);
        return List.of(new Attribute("order", order));
    }
}
