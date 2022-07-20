package io.getmedusa.medusa.core.util;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class FileUtils {

    private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

    private FileUtils() {}

    public static String load(String path) {
        try {
            path = FilenameUtils.normalize(path);
            Resource resource = resourceLoader.getResource("classpath:/" + path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load classpath file", e);
        }
    }
}
