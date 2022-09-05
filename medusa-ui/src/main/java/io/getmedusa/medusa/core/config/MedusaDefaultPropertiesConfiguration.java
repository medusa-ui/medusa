package io.getmedusa.medusa.core.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class MedusaDefaultPropertiesConfiguration implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Properties props = new Properties();

        props.put("server.port", 8080);
        props.put("spring.thymeleaf.prefix", "classpath:/");
        props.put("spring.rsocket.server.port", 7000);
        props.put("spring.rsocket.server.mapping-path", "/socket");
        props.put("spring.rsocket.server.transport", "websocket");
        props.put("spring.main.lazy-initialization", false);
        props.put("logging.level.root", "INFO");

        environment.getPropertySources().addLast(new PropertiesPropertySource("default-medusa-properties", props));
    }

}