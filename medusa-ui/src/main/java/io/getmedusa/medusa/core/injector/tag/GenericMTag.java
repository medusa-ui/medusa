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

    public static final Pattern patternTagWithMAttribute = Pattern.compile("<[^/]+ m-\\w+?=[\\s\"'].*?\\$.*?>", Pattern.CASE_INSENSITIVE);
    public static final Pattern patternMAttribute = Pattern.compile("m-\\w+?=[\\s\"'].*?\\$.*?[\"']", Pattern.CASE_INSENSITIVE);

    public InjectionResult injectWithVariables(InjectionResult html, Map<String, Object> variables) {
        String htmlString = html.getHtml();

        Matcher matcherFull = patternTagWithMAttribute.matcher(htmlString);
        while (matcherFull.find()) {
            final String tag = matcherFull.group().trim();
            String newTag = tag;

            final Set<String> classesToAdd = new HashSet<>();
            Matcher matcherTag = patternMAttribute.matcher(tag);
            while (matcherTag.find()) {
                String[] split = matcherTag.group().split("=\\s*[\"']");
                if(split.length == 2) {
                    String tagName = split[0].trim();
                    String expression = split[1].trim();
                    if(expression.endsWith("\"") || expression.endsWith("'")) expression = expression.substring(0, expression.length()-1);

                    GenericMAttribute attribute = GenericMAttribute.findValueByTagName(tagName);
                    classesToAdd.add(GenericMRegistry.getInstance().add(expression, attribute));

                    final String addition = ExpressionEval.evalAsBool(expression, variables) ? attribute.getValueWhenTrue() : attribute.getValueWhenFalse();
                    if(tagAlreadyExists(newTag, addition)) {
                        newTag = mergeTags(newTag, matcherTag.group(), addition);
                    } else {
                        newTag = newTag.replace(matcherTag.group(), addition);
                    }
                }
            }

            newTag = addMIdClasses(newTag, classesToAdd);
            newTag = clearDoubleSpacing(newTag);
            htmlString = htmlString.replace(tag, newTag);
        }

        html.setHtml(htmlString);
        return html;
    }

    private String mergeTags(String fullTag, String group, String addition) {
        String[] additionSplit = addition.split("=\"");
        String attribute = additionSplit[0];
        String content = additionSplit[1].substring(0, additionSplit[1].length()-1);
        return fullTag.replace(group, "").replace(attribute + "=\"", attribute + "=\"" + content + " ");
    }

    private boolean tagAlreadyExists(String fullTag, String addition) {
        return addition.contains("=\"") && fullTag.contains(" " + addition.split("=")[0] + "=");
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

