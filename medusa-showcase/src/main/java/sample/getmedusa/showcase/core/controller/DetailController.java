package sample.getmedusa.showcase.core.controller;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import sample.getmedusa.showcase.core.Versions;
import sample.getmedusa.showcase.core.model.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@UIEventPage(path = "/detail/{type}", file = "/pages/detail")
public class DetailController {

    public List<Attribute> setupAttributes(ServerRequest request) {
        final Component component = Component.findComponent(request.pathVariable("type"));
        final String title = component.getLabel();
        return List.of(new Attribute("title", title),
                new Attribute("type", request.pathVariable("type")),
                new Attribute("serverCode", loadCode(component.getServerCode())),
                new Attribute("clientCode", loadCode(component.getClientCode())),
                new Attribute("version", Versions.getVersionFooter()));
    }

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    public String[] loadCode(String ... paths) {
        List<String> code = new ArrayList<>();
        for(String path : paths) {
            Resource resource = resourceLoader.getResource("classpath:" + path);
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                code.add(FileCopyUtils.copyToString(reader));
            } catch (IOException e) {
                code.add("No code found");
            }
        }
        return code.toArray(new String[0]);
    }

}
