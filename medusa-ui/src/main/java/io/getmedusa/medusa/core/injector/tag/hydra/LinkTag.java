package io.getmedusa.medusa.core.injector.tag.hydra;

import io.getmedusa.medusa.core.injector.tag.AbstractTag;
import io.getmedusa.medusa.core.registry.hydra.HydraRegistry;
import org.springframework.stereotype.Component;

@Component
public class LinkTag extends AbstractTag {

    @Override
    protected String pattern() {
        return "(((m-link-style=\\\".+?\\\")|(m-link-style='.+?')).+)?((m-link=\\\".+?\\\")|(m-link='.+?'))(.+((m-link-style=\\\".+?\\\")|(m-link-style='.+?')))?";
    }

    @Override
    protected String substitutionLogic(String fullMatch, String tagContent) {
        final MLinkAttribute attribute = new MLinkAttribute(fullMatch);
        final String hrefAttribute = "href=\"#\" class=\"hydra-link\"";

        if("hide".equals(attribute.linkStyle)) {
            return hrefAttribute.replace("#", HydraRegistry.INSTANCE.lookupRoute(attribute.link)) + " style=\"display:none;\"";
        } else if("inactive".equals(attribute.linkStyle)) {
            return hrefAttribute.replace("class=\"hydra-link\"", "class=\"hydra-link disabled\"");
        }
        return hrefAttribute;
    }

    @Override
    protected String tagValue() {
        return "m-link";
    }

    static class MLinkAttribute {
        String link;
        String linkStyle;

        public MLinkAttribute(String fullMatch) {
            String[] attributes = fullMatch.split("\\s");
            for(String attribute : attributes) {
                final String trim = attribute.trim();
                if(trim.contains("=")) {
                    final String valueWithQuotes = trim.split("=")[1];
                    final String value = valueWithQuotes.substring(1, valueWithQuotes.length()-1);
                    if(trim.startsWith("m-link=")) {
                        this.link = value;
                    } else if(trim.startsWith("m-link-style=")) {
                        this.linkStyle = value;
                    }
                }
            }
        }
    }
}
