package io.getmedusa.medusa.core.boot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public enum FormDetection {

    INSTANCE;

    private final Map<String, Map<String, Class>> formObjects = new HashMap<>();

    void prepFile(final String html, Object bean) {
        if (html.contains(":{form}")) {
            final Document document = Jsoup.parse(html);
            final Elements elemsContainingForm = document.getElementsByAttributeValueContaining("m:submit", ":{form}");
            for(Element element : elemsContainingForm) {
                final ParsedOperation parsedOperation = parseOperation(element.attr("m:submit"));
                Class formClass = findArgInMethod(parsedOperation, bean);

                final String beanClass = bean.getClass().getName();
                Map<String, Class> classMap = formObjects.getOrDefault(beanClass, new HashMap<>());
                classMap.put(parsedOperation.operation(), formClass);
                formObjects.put(beanClass, classMap);
            }
        }
    }

    private Class findArgInMethod(ParsedOperation parsedOperation, Object bean) {
        for(Method method : bean.getClass().getMethods()) {
            if(method.getName().equals(parsedOperation.operation)) {
                return method.getParameterTypes()[parsedOperation.argIndex];
            }
        }
        return null;
    }

    private ParsedOperation parseOperation(String attr) {
        final String[] firstSplit = attr.split("\\(");
        final String operation = firstSplit[0];
        final String[] args = firstSplit[1].split(",");
        int formArg = 0;
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i].trim();
            if(arg.equals(":{form}")) {
                formArg = i;
                break;
            }
        }
        return new ParsedOperation(operation, formArg);
    }

    public Class<Object> getFormClass(String clazz, String methodName) {
        return formObjects.getOrDefault(clazz, new HashMap<>()).getOrDefault(methodName, null);
    }

    private record ParsedOperation(String operation, int argIndex) { }
}
