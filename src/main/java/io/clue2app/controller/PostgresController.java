package io.clue2app.controller;

import io.clue2app.entity.User;
import io.clue2app.service.PostgresService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@ConditionalOnProperty(name = "clue2app.postgres.enabled", havingValue = "true")
public class PostgresController {

	private final Logger logger = LoggerFactory.getLogger(PostgresController.class);

	static PostgresService service;

	public PostgresController(@Autowired PostgresService sqsService) {
		service = sqsService;
	}

	@GetMapping("/postgres/version")
	public ResponseEntity<String> checkConnection() {
		return new ResponseEntity<>(service.checkConnection(), HttpStatus.OK);
	}

	@GetMapping("/postgres")
	public ResponseEntity<List<User>> list() {
		return new ResponseEntity<>(service.list(), HttpStatus.OK);
	}


	@PostMapping("/postgres")
	ResponseEntity<User> create(@RequestBody final User user) {
		return new ResponseEntity<>(service.create(user), HttpStatus.OK);
	}

	@DeleteMapping("/postgres/{id}")
	ResponseEntity<Object> deleteFile(@PathVariable("id") final UUID id) {
		return new ResponseEntity<>(service.delete(id), HttpStatus.OK);
	}

}
