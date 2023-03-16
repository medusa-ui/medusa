package sample.getmedusa.showcase.core.controller;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@UIEventPage(path = "/install", file = "/pages/install")
public class InstallController {

    public List<Attribute> setupAttributes(){
        return List.of(new Attribute("samplePom", loadCode("/samples/sample-pom.xml")));
    }

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    public String loadCode(String path) {
        Resource resource = resourceLoader.getResource("classpath:" + path);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            return "No code found";
        }
    }
}
