package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.HydraRegistry;
import io.getmedusa.medusa.core.websocket.hydra.HydraMenuItem;
import io.getmedusa.medusa.core.websocket.hydra.meta.HydraStatus;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HydraMenuTag {

    public static final Pattern patternTagWithMenuAttribute = Pattern.compile("<[^<>]+? h-menu=[\"'].*?[\"']*?>", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternMenuAttribute = Pattern.compile("h-menu=[\"'].*?[\"']", Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult html) {
        String htmlString = html.getHTML();
        Matcher matcherFull = patternTagWithMenuAttribute.matcher(htmlString);
        while (matcherFull.find()) {
            final String tag = matcherFull.group().trim();

            StringBuilder tagContents = new StringBuilder();

            Matcher matcherTag = patternMenuAttribute.matcher(tag);
            while (matcherTag.find()) {
                final String rawHMenuName = matcherTag.group().trim();

                tagContents.append(tag.replace(rawHMenuName, ""));

                String menuName = rawHMenuName.replace("h-menu=", "").substring(1);
                menuName = menuName.substring(0, menuName.length() - 1);

                tagContents.append("<ul h-menu=\"");
                tagContents.append(menuName);
                tagContents.append("\">");

                final HydraStatus status = HydraRegistry.getStatus();
                if(null != status) {
                    final Set<HydraMenuItem> menuItems = status.getMenuItems().get(menuName);
                    if(menuItems != null) {
                        for (HydraMenuItem menuItem : menuItems) {
                            tagContents.append(toHTMLMenuItem(menuItem));
                        }
                    }
                }

                tagContents.append("</ul>");
            }

            htmlString = htmlString.replace(tag, tagContents);
        }

        //html.setHtml(htmlString);

        return html;
    }

    private String toHTMLMenuItem(HydraMenuItem menuItem) {
        return "<li><a href=\"" + menuItem.endpoint() + "\">" + menuItem.labelWithFallback() + "</a></li>";
    }
}
