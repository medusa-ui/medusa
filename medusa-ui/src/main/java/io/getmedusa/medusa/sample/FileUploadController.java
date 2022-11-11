package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@UIEventPage(path = "/file", file = "/pages/file")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    public List<Attribute> upload(NamedFile data){
        logger.info("NamedFile: {} ",data);
        return List.of();
    }

    public record NamedFile(String name, Object file){}
}
