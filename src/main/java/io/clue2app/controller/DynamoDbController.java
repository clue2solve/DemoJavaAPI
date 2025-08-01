package io.clue2app.controller;

import io.clue2app.service.DynamoDbService;
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

import java.util.Map;

@RestController
@ConditionalOnProperty(name = "clue2app.dynamodb.enabled", havingValue = "true")
public class DynamoDbController {

	private final Logger logger = LoggerFactory.getLogger(DynamoDbController.class);

	static DynamoDbService service;

	public DynamoDbController(@Autowired DynamoDbService dynamodbService) {
		this.service = dynamodbService;
	}

	@GetMapping("/dynamodb/{id}")
	public ResponseEntity<Object> get(@PathVariable("id") final String id) {
		return new ResponseEntity<>(service.get(id), HttpStatus.OK);
	}


	@PostMapping("/dynamodb")
	ResponseEntity<Object> create(@RequestBody Map<String, Object> payload) {
		return new ResponseEntity<>(service.create(payload), HttpStatus.OK);
	}

	@DeleteMapping("/dynamodb/{id}")
	ResponseEntity<Object> delete(@PathVariable("id") final String id) {
		service.delete(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
