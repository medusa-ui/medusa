package sample.getmedusa.showcase.core.controller;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractController {

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
