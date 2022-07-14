package sample.getmedusa.showcase.samples;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public abstract class AbstractSampleController {

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    public String controllerCodeAsString(Object controller) {
        Resource resource = resourceLoader
                .getResource("classpath:" + controller.getClass().getName()
                .replace("sample.getmedusa.showcase.", "")
                .replace(".", "/") + ".txt");
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String pageCodeAsString(Object controller) {
        Resource resource = resourceLoader
                .getResource("classpath:" + controller.getClass().getName()
                        .replace("sample.getmedusa.showcase.", "")
                        .replace(".", "/") + "_page.txt");
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
