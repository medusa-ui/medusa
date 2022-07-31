package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@UIEventPage(path = "/clicks", file = "/pages/clicks")
public class ClicksController {

    private static final Logger logger = LoggerFactory.getLogger(ClicksController.class);

    public List<Attribute> setupAttributes(){
        logger.info("setupAttributes()");
        return List.of(
                    new Attribute("number", 42),
                    new Attribute("text", "some text"),
                    new Attribute("one", new KeyValue("one",1)),
                    new Attribute("two", new KeyValue("two", new KeyValue("three",3)))
                );
    }

    public List<Attribute> action() {
        logger.info("action()");
        return List.of(
                new Attribute("action", "action()")
        );
    }

    public List<Attribute> action(Integer number) {
        logger.info("action(Integer number)");
        return List.of(
                new Attribute("action", "action(Integer number)"),
                new Attribute("value", number)
        );
    }

    public List<Attribute> action(String text) {
        logger.info("action(String text) ");
        return List.of(
                new Attribute("action", "action(String text)"),
                new Attribute("value", text)
        );
    }

    public List<Attribute> action(Integer number, String text) {
        logger.info("action(Integer number, String text)");
        return List.of(
                new Attribute("action", "action(Integer number, String text)"),
                new Attribute("value", number + " and " + text)
        );
    }
}

class KeyValue{
    String name;
    Object target;
    KeyValue(String name, Object target) {
        this.name = name;
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public Object getTarget() {
        return target;
    }
}
