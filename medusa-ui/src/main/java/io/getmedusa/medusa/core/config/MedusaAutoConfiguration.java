package io.getmedusa.medusa.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * This is autoconfiguration that gets called by /META-INF/spring.factories. <br/>
 * It simply ensures the beans defined in the medusa library are component scanned
 */
@Configuration
@ComponentScan("io.getmedusa")
class MedusaAutoConfiguration {

}
