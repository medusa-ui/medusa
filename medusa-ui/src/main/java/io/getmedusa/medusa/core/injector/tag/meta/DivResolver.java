package io.getmedusa.medusa.core.injector.tag.meta;

import io.getmedusa.medusa.core.registry.IterationRegistry;
import io.getmedusa.medusa.core.util.ExpressionEval;
import io.getmedusa.medusa.core.util.IdentifierGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DivResolver {

    private static final String TAG_EACH = "[$each";
    private static final String TAG_THIS_EACH = "[$this.each";

    private static final Pattern PROPERTY_PATTERN =  Pattern.compile("\\[\\$(this\\.|(parent\\.){1,99})each\\.?.*?]", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);


    public static String resolve(Div div) {
        return parseEachContent(div.getChainOnThisLevel(), getHtmlToReplace(div));
    }

    private static String getHtmlToReplace(Div div) {
        if(div.getResolvedHTML().equals("")) {
            return div.getElement().innerHTML;
        } else {
            return div.getResolvedHTML();
        }
    }

    public static String parseEachContent(ParentChain parentChain, String divContent) {
        System.out.println(divContent);
        divContent = divContent.replace(TAG_EACH, TAG_THIS_EACH); //the use of [$each] is optional and is equal to using [$this.each]

        final Object eachObject = parentChain.getEachObject();

        divContent = divContent.replace(TAG_THIS_EACH + ']', eachObject.toString());

        Matcher matcher = PROPERTY_PATTERN.matcher(divContent);
        while (matcher.find()) {
            final String replace = matcher.group(); // $[this.each.property] or $[parent.parent.each.property]
            //so everything up to the .each. determines what the eachObject is going to be used in this expression

            final String eachPropertyDeterminator = replace.substring(2, replace.indexOf(".each"));
            Object transitiveEachObject = eachObject;
            if(!"this".equals(eachPropertyDeterminator)) {
               //resolve parents
                int steps = eachPropertyDeterminator.split("parent").length;
                ParentChain relevantChain = parentChain.getParent();
                for (int i = 0; i < steps; i++) {
                    relevantChain = relevantChain.getParent();
                }
                transitiveEachObject = relevantChain.getEachObject();
            }
            String evalObject = transitiveEachObject.toString();
            if(replace.contains(".each.")) {
                final String property = replace.substring(replace.indexOf(".each.") + 6, replace.length() - 1).trim();
                evalObject = ExpressionEval.evalObject(property, transitiveEachObject);
            }
            divContent = divContent.replace(replace, evalObject);
        }

        return divContent;
    }

    public static String wrapInDiv(Div div, String divContent) {
        //TODO do better
        ForEachElement element = div.getElement();
        final String templateID = IdentifierGenerator.generateTemplateID(element);
        int index = 0;
        //
        return "<div index=\"" + index + "\" template-id=\"" + templateID + "\">\n" + divContent + "\n</div>\n";
    }

    public static String buildTemplate(Div div) {
        ForEachElement element = div.getElement();
        final String templateID = IdentifierGenerator.generateTemplateID(element);
        IterationRegistry.getInstance().add(templateID, element.condition);
        return "<template m-id=\"" + templateID + "\">" + div.getElement().innerHTML.replace(TAG_EACH, TAG_THIS_EACH) + "</template>";
    }

}
