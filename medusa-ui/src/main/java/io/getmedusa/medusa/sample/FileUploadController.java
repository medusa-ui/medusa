package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@UIEventPage(path = "/file", file = "/pages/file")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    public List<Attribute> upload(NamedFile namedFile) {
        return List.of(new Attribute("upload", namedFile.document));
    }

    public record NamedFile(String name, File document) { }

}
