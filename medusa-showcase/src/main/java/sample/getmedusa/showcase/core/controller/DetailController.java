package sample.getmedusa.showcase.core.controller;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;
import io.getmedusa.medusa.core.bidirectional.ServerToClient;
import io.getmedusa.medusa.core.session.StandardSessionTagKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import sample.getmedusa.showcase.core.model.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@UIEventPage(path = "/detail/{type}", file = "/pages/detail")
public class DetailController {

    public List<Attribute> setupAttributes(ServerRequest request) {
        final Component component = Component.findComponent(request.pathVariable("type"));
        final String title = component.getLabel();
        return List.of(new Attribute("title", title),
                new Attribute("type", request.pathVariable("type")),
                new Attribute("serverCode", loadCode(component.getServerCode())),
                new Attribute("clientCode", loadCode(component.getClientCode())));
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

    @Autowired
    private ServerToClient serverToClient;

    @Scheduled(fixedDelay = 500)
    public void aServerProcess() {
        //global
        serverToClient.sendAttributesToSessionTag(
                List.of(new Attribute("randomNumberShared", randomInt())),
                StandardSessionTagKeys.ROUTE,
                "/detail/live-data");

    }

    private int randomInt() {
        return new Random().nextInt(99999);
    }

}
