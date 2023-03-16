package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@UIEventPage(path = "th-if",file = "/pages/th-if")
public class ThIfController {
    private static final Logger logger = LoggerFactory.getLogger(ThIfController.class);
    int counter;


    public List<Attribute> setupAttributes(){
        return List.of(
                new Attribute("top",true),
                new Attribute("middle",true),
                new Attribute("bottom",true)
        );
    }

    public List<Attribute> change(){
        counter ++;
        if(counter == 4) {
            counter = 0;
        }
        boolean top = counter != 1;
        boolean middle = counter != 2;
        boolean bottom = counter != 3;
        logger.debug("counter: {}, top: {}, middle: {}, bottom: {}", counter, top, middle, bottom);
        return List.of(
                new Attribute("top",top ),
                new Attribute("middle", middle),
                new Attribute("bottom", bottom)
        );
    }

}