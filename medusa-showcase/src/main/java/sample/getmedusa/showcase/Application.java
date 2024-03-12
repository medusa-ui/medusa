package sample.getmedusa.showcase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import sample.getmedusa.showcase.core.Versions;

@SpringBootApplication
public class Application {

	@Value("${medusa.version}")
	private String medusaVersion;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	void setVersions() {
		Versions.V_JDK = System.getProperty("java.version");
		Versions.V_MEDUSA_UI = medusaVersion;
	}

}
