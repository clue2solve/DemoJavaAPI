package io.clue2app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		final Map<String, String> vars = System.getenv();
		vars.forEach((key, value) -> logger.info(String.format("%s => %s", key, value)));

		SpringApplication.run(Application.class, args);
	}

}