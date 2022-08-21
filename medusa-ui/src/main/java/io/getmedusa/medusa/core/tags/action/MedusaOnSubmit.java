package io.getmedusa.medusa.core.tags.action;

import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.session.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class MedusaOnSubmit {

    /*
    <form m:submit="displayName(123, :{form}, 'sample')">
     <label for="firstName">First name: </label> <input type="text" id="firstName" name="firstName" value="도윤" />
     <label for="lastName">Last name: </label> <input type="text" id="lastName" name="lastName" value="김" />
     <input type="submit" value="Submit">
    </form>
    */

    /*
    <form m:submit="displayName(123, :{form}, 'sample')">
     <label for="firstName">First name: </label> <input type="text" id="firstName" name="firstName" value="도윤" />
     <label for="lastName">Last name: </label> <input type="text" id="lastName" name="lastName" value="김" />
     <input type="submit" value="Submit" m:click="displayName(123, :{form}, 'sample')">
    </form>
    */

    public String handleSubmit(String html, Session session) {
        if(html.contains(" m:submit=")) {
            Document document = Jsoup.parse(html);
            Elements forms  = document.getElementsByAttribute("m:submit");

            for(Element form : forms) {
                final String operation = form.attr("m:submit");
                form.removeAttr("m:submit");
                form.attr("onsubmit", "event.preventDefault();");

                //TODO know if map or form object; for now let's assume a map! Map<String, Object> form
                //find names and build into form obj or map

                Map<String, Object> objectMap = new HashMap<>();
                Elements namedElements = form.getElementsByAttribute("name");
                for(Element element : namedElements) {
                    objectMap.put(element.attr("name"), element.val());
                }

                final String formId = "form000" + UUID.randomUUID().toString().replace("-", "");
                session.getLastParameters().add(new Attribute(formId, objectMap));

                //find submit and make into click
                for(Element submitButton : form.getElementsByAttributeValue("type", "submit")) {
                    submitButton.attr("m:click", operation.replace(":{form}", "#" + formId)); //TODO this does not get added to fragment for some reason
                }
            }
            return document.html();
        }
        return html;
    }
}