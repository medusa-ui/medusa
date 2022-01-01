package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.HydraRegistry;
import io.getmedusa.medusa.core.websocket.hydra.HydraMenuItem;
import io.getmedusa.medusa.core.websocket.hydra.meta.HydraStatus;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;
import java.util.Set;

public class HydraMenuTag extends AbstractTag {

    @Override
    public InjectionResult inject(InjectionResult result, Map<String, Object> variables, ServerRequest request) {
        Elements hMenus = result.getDocument().getElementsByAttribute(TagConstants.H_MENU);
        for(Element hMenu : hMenus) {
            final String menuName = hMenu.attr(TagConstants.H_MENU);
            hMenu.removeAttr(TagConstants.H_MENU);

            Element ul = createUL(menuName);

            final HydraStatus status = HydraRegistry.getStatus();
            if(null != status) {
                final Set<HydraMenuItem> menuItems = status.getMenuItems().get(menuName);
                if(menuItems != null) {
                    for (HydraMenuItem menuItem : menuItems) {
                        ul.appendChild(createLI(menuItem.endpoint(), menuItem.labelWithFallback()));
                    }
                }
            }

            hMenu.appendChild(ul);
        }

        return result;
    }

    private Element createUL(String menuName) {
        return new Element("ul").attr(TagConstants.H_MENU_ATTR, menuName);
    }

    private Element createLI(String url, String name) {
        Element li = new Element("li");
        Element a = new Element("a");
        a.attr("href", url);
        a.text(name);
        li.appendChild(a);
        return li;
    }

}
