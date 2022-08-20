package sample.getmedusa.showcase.core.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Component {

    public static final Map<String, List<Component>> MAP = buildCategories();

    private static Map<String, List<Component>> buildCategories() {
        final Map<String, List<Component>> map = new LinkedHashMap<>();

        map.put("Button", List.of(
                new Component("Basic button", "basic-button",
                        new String[]{"/samples/button/basic/page.txt"},
                        new String[]{"/samples/button/basic/controller.txt"}),
                new Component("Conditional button")
        ));

        map.put("Text inputs", List.of(
                new Component("Submit with reference"),
                new Component("Form submit tag", "form-submit",
                        new String[]{"/samples/button/basic/page.txt"},
                        new String[]{"/samples/button/basic/controller.txt"}),
                new Component("Search"),
                new Component("Validation")
        ));

        map.put("Var inputs", List.of(
                new Component("File uploads"),
                new Component("Option list", "option-list",
                        new String[]{"/samples/input/special/select/basic/page.txt","/samples/input/special/select/linked/page.txt"},
                        new String[]{ "/samples/input/special/select/basic/controller.txt", "/samples/input/special/select/linked/controller.txt"}
                ),
                new Component("Multiple selection list")
        ));

        map.put("Live data", List.of(
                new Component("Serverside live data", "live-data",
                        new String[]{"/samples/live/statistic/Shared_page.txt", "/samples/live/statistic/PerSession_page.txt", "/samples/live/statistic/PerGroup_page.txt"},
                        new String[]{"/samples/live/statistic/Shared.txt", "/samples/live/statistic/PerSession.txt", "/samples/live/statistic/PerGroup.txt"}),
                new Component("Live table"),
                new Component("Progress bar")
        ));

        map.put("Overlays", List.of(
                new Component("Modal / Alert"),
                new Component("Notifications"),
                new Component("Loading")
        ));

        map.put("Navigation", List.of(
                new Component("Forwarding", "forwarding",
                        new String[]{"/samples/navigation/ForwardingController_page.txt", "/samples/navigation/ForwardingServerInitController_page.txt"},
                        new String[]{"/samples/navigation/ForwardingController.txt", "/samples/navigation/ForwardingServerInitController.txt"}),
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
    private final boolean comingSoon;

    private final String[] serverCode;
    private final String[] clientCode;

    public Component(String label, String urlPart, String[] clientCode, String[] serverCode) {
        this.label = label;
        this.urlPart = urlPart;
        this.comingSoon = false;
        this.serverCode = serverCode;
        this.clientCode = clientCode;
    }

    public Component(String label, String urlPart) {
        this.label = label;
        this.urlPart = urlPart;
        this.comingSoon = false;
        this.serverCode = new String[0];
        this.clientCode = new String[0];
    }

    public Component(String label) {
        this.label = label;
        this.urlPart = "";
        this.comingSoon = true;
        this.serverCode = new String[0];
        this.clientCode = new String[0];
    }

    public String getLabel() {
        return label;
    }

    public String getUrlPart() {
        return urlPart;
    }

    public boolean isComingSoon() {
        return comingSoon;
    }

    public String[] getClientCode() {
        return clientCode;
    }

    public String[] getServerCode() {
        return serverCode;
    }
}
