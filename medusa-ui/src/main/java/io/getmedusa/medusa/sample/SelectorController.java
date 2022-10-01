package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;

@UIEventPage(path = "/selectors", file = "/pages/selectors")
public class SelectorController {

    public List<Attribute> numbers(List<Integer> numbers){
        String received = "List<Integer> numbers: %s ".formatted(numbers);
        return List.of(new Attribute("received", received));
    }

    public List<Attribute> checkboxes(List<String> values){
        String received = "List<String> values: %s".formatted(values);
        return List.of(new Attribute("received", received));
    }

    public List<Attribute> mixed(List<String> values, String single, List<Integer> numbers ){
        String received = "List<String> values: %s, String single: %s, List<Integer> numbers %s".formatted(values,single,numbers);
        return List.of(new Attribute("received", received));
    }
}
