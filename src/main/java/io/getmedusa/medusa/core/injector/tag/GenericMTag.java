package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.GenericMAttribute;
import io.getmedusa.medusa.core.injector.tag.meta.InjectionResult;
import io.getmedusa.medusa.core.registry.GenericMRegistry;
import io.getmedusa.medusa.core.util.ExpressionEval;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericMTag {

    public static final Pattern patternTagWithMAttribute = Pattern.compile("<.*? m-\\w+?=\".*?\\$.*?>", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternMAttribute = Pattern.compile("m-\\w+?=\".*?\\$.*?\"", Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult html, Map<String, Object> variables) {
        String htmlString = html.getHtml();

        Matcher matcherFull = patternTagWithMAttribute.matcher(htmlString);
        while (matcherFull.find()) {
            final String tag = matcherFull.group().trim();
            String newTag = tag;

            final Set<String> classesToAdd = new HashSet<>();
            Matcher matcherTag = patternMAttribute.matcher(tag);
            while (matcherTag.find()) {
                String[] split = matcherTag.group().split("=\"");
                if(split.length == 2) {
                    String tagName = split[0].trim();
                    String expression = split[1].trim();
                    if(expression.endsWith("\"")) expression = expression.substring(0, expression.length()-1);

                    GenericMAttribute attribute = GenericMAttribute.findValueByTagName(tagName);
                    classesToAdd.add(GenericMRegistry.getInstance().add(expression, attribute));

                    newTag = newTag.replace(matcherTag.group(), ExpressionEval.evalAsBool(expression, variables) ? attribute.getValueWhenTrue() : attribute.getValueWhenFalse());
                    //TODO what if a style is already defined? what if we add a class here?
                }
            }

            newTag = addMIdClasses(newTag, classesToAdd);
            newTag = clearDoubleSpacing(newTag);
            htmlString = htmlString.replace(tag, newTag);
        }

        html.setHtml(htmlString);
        return html;
    }

    private String addMIdClasses(String newTag, Set<String> classesToAdd) {
        StringBuilder classBuilder = new StringBuilder();
        for(String classToAdd : classesToAdd) {
            classBuilder.append(" ");
            classBuilder.append(classToAdd);
        }

        if(newTag.contains("class=")) {
            newTag = newTag.replace("class=\"", "class=\"" + classBuilder.toString().trim() + " ");
        } else {
            newTag = newTag.replace(">", " class=\"" + classBuilder.toString().trim() + "\">");
        }
        return newTag;
    }

    private String clearDoubleSpacing(String newTag) {
        while(newTag.contains("  ")) {
            newTag = newTag.replace("  ", " ");
        }
        return newTag;
    }

}

