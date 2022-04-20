package io.getmedusa.medusa.core.util;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class WrapperUtils {

    public static Element wrap(Element elementToWrap) {
        return wrap(elementToWrap, null);
    }

    public static Element wrapAndReplace(Element elementToWrap) {
        return wrapAndReplace(elementToWrap, null);
    }

    public static Element wrap(Element elementToWrap, String className) {
        if(elementToWrap == null) return null;
        return elementToWrap.wrap(divWClassAsString(className)).parent();
    }

    public static Element wrap(Elements elementsToWrap) {
        return wrap(elementsToWrap, null);
    }

    public static Element wrap(Elements elementsToWrap, String className) {
        if(elementsToWrap == null) return null;

        Element wrapper = wrap(elementsToWrap.first(), className);
        if(elementsToWrap.size() > 1) wrapper.appendChildren(allButFirst(elementsToWrap));
        return wrapper;
    }

    private static List<Element> allButFirst(Elements elementsToWrap) {
        return elementsToWrap.subList(1, elementsToWrap.size());
    }

    public static Element wrapAndReplace(Element elementToWrap, String className) {
        if(elementToWrap == null) return null;
        Element wrapper = divWClass(className);
        wrapper.appendChildren(elementToWrap.children());
        elementToWrap.replaceWith(wrapper);
        return wrapper;
    }

    private static String divWClassAsString(String className) {
        Element div = divWClass(className);
        return div.outerHtml();
    }

    private static Element divWClass(String className) {
        Element div = new Element("div");
        if(className != null && !className.equals("")) {
            div.addClass(className);
        }
        return div;
    }

    public static Object wrapObject(Object potentialToWrap) {
        if(potentialToWrap instanceof String string &&
            !TypeUtils.isNumeric(string) &&
            !TypeUtils.isBoolean(string)) {
            return wrapString(string);
        }
        return potentialToWrap;
    }

    public static Object wrapString(String potentialToWrap) {
        if(!isQuoted(potentialToWrap)) {
            return "'" + potentialToWrap.replace("'", "\\'") +  "'";
        } else {
            return potentialToWrap;
        }
    }

    public static boolean isQuoted(Object potentialToWrap) {
        if(potentialToWrap instanceof String) {
            final String str = potentialToWrap.toString();
            return (str.startsWith("'") && str.endsWith("'")) || (str.startsWith("\"") && str.endsWith("\""));
        }
        return false;
    }
}
