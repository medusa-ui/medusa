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
    protected static final int ENDFOR_LENGTH = "[$end for]".length();

    public static String resolve(Div div) {
        return parseEachContent(div.getChainOnThisLevel(), getHtmlToReplace(div));
    }

    public static String getHtmlToReplace(Div div) {
        if(div.getResolvedHTML().equals("")) {
            return div.getElement().innerHTML;
        } else {
            return div.getResolvedHTML();
        }
    }

    public static String parseEachContent(ParentChain parentChain, String divContent) {
        if(parentChain == null) return divContent;

        divContent = divContent.replace(TAG_EACH, TAG_THIS_EACH).trim(); //the use of [$each] is optional and is equal to using [$this.each]

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
                int steps = eachPropertyDeterminator.split("parent").length - 1;
                if(steps < 0) steps = 0;
                ParentChain relevantChain = parentChain.getParent();
                if(null != relevantChain) {
                    for (int i = 0; i < steps; i++) {
                        if(relevantChain.getParent() != null) relevantChain = relevantChain.getParent();
                    }
                    transitiveEachObject = relevantChain.getEachObject();
                }
            }
            String evalObject = "";
            if(transitiveEachObject != null) {
                evalObject = transitiveEachObject.toString();
                if(replace.contains(".each.")) {
                    final String property = replace.substring(replace.indexOf(".each.") + 6, replace.length() - 1).trim();
                    evalObject = ExpressionEval.evalObject(property, transitiveEachObject);
                }
            }
            divContent = divContent.replace(replace, evalObject);
        }

        return divContent;
    }

    public static String wrapInDiv(Div div, String divContent) {
        ForEachElement element = div.getElement();
        final String templateID = IdentifierGenerator.generateTemplateID(element);
        int index = 0;
        return "<div index=\"" + index + "\" template-id=\"" + templateID + "\">" + divContent + "</div>";
    }

    public static String buildTemplate(ForEachElement element) {
        String innerHTML = element.innerHTML;

        for(ForEachElement child : element.getChildren()) {
            String childTemplate = buildTemplate(child);

            /*String relevantBlock = innerHTML;
            if(innerHTML.contains("[$foreach"))
                relevantBlock = innerHTML.substring(innerHTML.indexOf("[$foreach"), innerHTML.indexOf("[$end for]") + ENDFOR_LENGTH);*/

            innerHTML = innerHTML.replace(child.blockHTML, childTemplate);
        }

        final String templateID = IdentifierGenerator.generateTemplateID(element);
        IterationRegistry.getInstance().add(templateID, element.condition);

        return "<template m-id=\"" + templateID + "\">" + innerHTML.replace(TAG_EACH, TAG_THIS_EACH) + "</template>";
    }

}
