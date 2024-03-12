package sample.getmedusa.showcase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import sample.getmedusa.showcase.core.Versions;

@SpringBootApplication
public class Application {

	@Value("${java.version:21}") String javaVersion;
	@Value("${medusa.version:0.9.5}") String medusaVersion;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	void setVersions() {
		Versions.V_JDK = javaVersion;
		Versions.V_MEDUSA_UI = medusaVersion;
	}

}