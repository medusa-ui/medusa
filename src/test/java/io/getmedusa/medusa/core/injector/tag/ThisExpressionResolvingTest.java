package io.getmedusa.medusa.core.injector.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThisExpressionResolvingTest {
    String html =
            "<h5>Orders</h5>\n" +
            "<div>\n" +
            "    <button m-click='order(\"Yellow Sky\")' type=\"button\">Yellow Sky</button>\n" +
            "    <button m-click='order(this.value, this.m-hide)' m-hide=\"$hidden\" type=\"button\">Dark Sky</button>\n" +
            "    <button m-click='order(this.value)' type=\"button\" value=\"Empty Sky\">PROMO</button>\n" +
            "    <button m-click='order(this.value, this.type)' type=\"button\" value='Red Sky'>Surprise me</button>\n" +
            "</div>\n" +
            "<br>";

    String simpleEmptySky = "<button m-click='order(this.value)' type=\"button\" value=\"Empty Sky\">PROMO</button>";
    String complexEmptySky = "<button m-click='order(this.value, this.type)' type=\"button\" value=\"Empty Sky\">PROMO</button>";

    String darkSkyTagValue="<button m-click='order(this.value)' type=\"button\">Dark Sky</button>";

    @Test
    @Order(1)
    @DisplayName("find attribute names in expressions like 'this.value'")
    void findAttributeNamesFromTagWithThisExpression() {
        //when
        List<String> simple = ThisValueMatcher.attributeNameFromExpression(simpleEmptySky);
        //then
        Assertions.assertEquals(1, simple.size(),"size should be 1");
        Assertions.assertEquals("value", simple.get(0), "value should be found");

        // when
        List<String> complex = ThisValueMatcher.attributeNameFromExpression(complexEmptySky);
        // then
        Assertions.assertEquals(2, complex.size(),"size should be 2");
        Assertions.assertEquals("value", complex.get(0), "value should be found");
        Assertions.assertEquals("type", complex.get(1), "type should be found");
    }

    @Test
    @Order(2)
    @DisplayName("determine value a referenced attribute in expressions like 'this.value'")
    void determineValueOfAttribute(){
        // given
        String attributeValue = ThisValueMatcher.attributeNameFromExpression(simpleEmptySky).get(0);
        String attributeType =  ThisValueMatcher.attributeNameFromExpression(complexEmptySky).get(1);
        String tagValue = ThisValueMatcher.attributeNameFromExpression(darkSkyTagValue).get(0);

        // when
        String emptySky = ThisValueMatcher.attributeValueTag(simpleEmptySky, attributeValue);
        String button = ThisValueMatcher.attributeValueTag(complexEmptySky, attributeType);
        String darkSky = ThisValueMatcher.attributeValueTag(darkSkyTagValue, tagValue);

        // then
        Assertions.assertEquals("Empty Sky", emptySky);
        Assertions.assertEquals("button", button);
        Assertions.assertEquals("Dark Sky", darkSky);

    }

    @Test
    @Order(3)
    @DisplayName("replace resolved this-expressions in tags")
    void replaceResolvedThisExpressionInTags(){
        // when
        String emptySkyTag = ThisValueMatcher.resolveThisExpressionsInTag(simpleEmptySky);
        String darkSkyTagValueTag = ThisValueMatcher.resolveThisExpressionsInTag(darkSkyTagValue);
        String complexEmptySkyTag = ThisValueMatcher.resolveThisExpressionsInTag(complexEmptySky);

        // then
        Assertions.assertEquals("<button m-click='order(\"Empty Sky\")' type=\"button\" value=\"Empty Sky\">PROMO</button>", emptySkyTag);
        Assertions.assertEquals("<button m-click='order(\"Dark Sky\")' type=\"button\">Dark Sky</button>", darkSkyTagValueTag);
        Assertions.assertEquals("<button m-click='order(\"Empty Sky\", \"button\")' type=\"button\" value=\"Empty Sky\">PROMO</button>", complexEmptySkyTag);
    }

    @Test
    @Order(4)
    @DisplayName("replace this-expressions in HTML")
    void replaceResolvedThisExpressionInHTML() {
        // when
        String resolvedHtml = ThisValueMatcher.resolveThisExpressions(html);
        // then
        String expectedHtml =
                "<h5>Orders</h5>\n" +
                "<div>\n" +
                "    <button m-click='order(\"Yellow Sky\")' type=\"button\">Yellow Sky</button>\n" +
                "    <button m-click='order(\"Dark Sky\", \"$hidden\")' m-hide=\"$hidden\" type=\"button\">Dark Sky</button>\n" +
                "    <button m-click='order(\"Empty Sky\")' type=\"button\" value=\"Empty Sky\">PROMO</button>\n" +
                "    <button m-click='order(\"Red Sky\", \"button\")' type=\"button\" value='Red Sky'>Surprise me</button>\n" +
                "</div>\n" +
                "<br>";
        Assertions.assertEquals(expectedHtml,resolvedHtml);

    }
}

abstract class ThisValueMatcher {
    static String attributeValuePatternPostFix = "\\s?=\\s?[\"|']([^\"|']*)";

    static Pattern htmlWithThisExpression = Pattern.compile("<(.*?) m-\\w+?=\\s?[\"|'].*this.*?>.*</(\\1)>");
    static Pattern attributePattern = Pattern.compile("this\\.([\\w|-]+)");
    static Pattern tagValuePattern = Pattern.compile(">(.*)<");

    static List<String> attributeNameFromExpression(String tag) {
        List<String> found = new ArrayList<>();
        Matcher matcher = attributePattern.matcher(tag);
        while(matcher.find()){
            found.add(matcher.group(1));
        }
        return found;
    }

    static String attributeValueTag(String tag, String attribute) {
        String value = null;
        Pattern pattern = Pattern.compile(attribute + attributeValuePatternPostFix);
        Matcher matcher = pattern.matcher(tag);
        if(matcher.find(1)) {
            value = matcher.group(1);
        } else {
            Matcher tagMatcher = tagValuePattern.matcher(tag);
            if(tagMatcher.find(1)) value = tagMatcher.group(1);
        }
        return value;
    }

    static String resolveThisExpressionsInTag(String tag){
        String result = tag;
        for (String attribute:attributeNameFromExpression(tag)){
            String toReplace = "\"" + attributeValueTag(tag, attribute) + "\"";
            result = result.replace("this."+attribute, toReplace);
        }
        return result;
    }

    static String resolveThisExpressions(String html){
        String result = html;
        Matcher matcher = htmlWithThisExpression.matcher(result);
        while(matcher.find()){
            String tag = matcher.group();
            String resolvedTag = resolveThisExpressionsInTag(tag);
            result = result.replace(tag, resolvedTag);
        }
        return result;
    }

}
