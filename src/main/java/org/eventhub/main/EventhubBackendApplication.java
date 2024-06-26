package org.eventhub.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EventhubBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventhubBackendApplication.class, args);
	}

}
