package io.clue2app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	private final Logger logger = LoggerFactory.getLogger(HealthController.class);

	@Value("${version}")
	private String version;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/health")
	public String health() {
		return "OK";
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/version")
	public String version() {
		return version;
	}

}
