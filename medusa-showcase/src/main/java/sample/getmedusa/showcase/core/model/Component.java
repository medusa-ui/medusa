package sample.getmedusa.showcase.core.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Component {

    public static final Map<String, List<Component>> MAP = buildCategories();

    private static Map<String, List<Component>> buildCategories() {
        final Map<String, List<Component>> map = new LinkedHashMap<>();

        map.put("Button", List.of(
                new Component("Basic button"),
                new Component("Conditional button")
        ));

        map.put("Text inputs", List.of(
                new Component("Submit with reference"),
                new Component("Form submit tag"),
                new Component("Search"),
                new Component("Validation")
        ));

        map.put("Special inputs", List.of(
                new Component("File uploads"),
                new Component("Option list"),
                new Component("Multiple selection list")
        ));

        map.put("Live data", List.of(
                new Component("Live statistic"),
                new Component("Live table"),
                new Component("Progress bar")
        ));

        map.put("Overlays", List.of(
                new Component("Modal / Alert"),
                new Component("Notifications"),
                new Component("Loading")
        ));

        map.put("Navigation", List.of(
                new Component("Menubar")
        ));

        map.put("Embedding", List.of(
                new Component("Fragment")
        ));

        return map;
    }

    public static Component findComponent(String type) {
        for(List<Component> categoryList : Component.MAP.values()) {
            for(Component component : categoryList) {
                if(type.equals(component.getUrlPart())) {
                    return component;
                }
            }
        }
        throw new IllegalArgumentException("Type has no matching category");
    }

    private final String label;
    private final String urlPart;


    public Component(String label) {
        this.label = label;
        this.urlPart = label.toLowerCase()
                .replace(" / ", " ")
                .replace(" ", "-");
    }

    public String getLabel() {
        return label;
    }

    public String getUrlPart() {
        return urlPart;
    }
}
