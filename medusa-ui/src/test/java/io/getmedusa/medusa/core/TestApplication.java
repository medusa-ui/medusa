package io.getmedusa.medusa.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

@SpringBootApplication
public class TestApplication {

	public static void main(String[] args) {
		errorOnAnyBlockingCalls();
		SpringApplication.run(TestApplication.class, args);
	}

	private static void errorOnAnyBlockingCalls() {
		final BlockHound.Builder builder = BlockHound.builder();
		ServiceLoader<BlockHoundIntegration> serviceLoader = ServiceLoader.load(BlockHoundIntegration.class);
		StreamSupport.stream(serviceLoader.spliterator(), false)
				.sorted()
				.forEach(builder::with);
		builder.allowBlockingCallsInside("org.springframework.http.MediaTypeFactory", "parseMimeTypes"); //https://github.com/spring-projects/spring-framework/issues/26631#issuecomment-791504853
		builder.install();
	}

}
