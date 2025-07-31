package io.clue2app.controller;

import io.clue2app.service.SqsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SqsController {

	private final Logger logger = LoggerFactory.getLogger(SqsController.class);

	static SqsService service;

	public SqsController(@Autowired SqsService sqsService) {
		service = sqsService;
	}

	@GetMapping("/sqs/getOne")
	public ResponseEntity<Object> getOne() {
		return new ResponseEntity<>(service.getOne(), HttpStatus.OK);
	}

	@PostMapping("/sqs/send")
	ResponseEntity<Object> send(@RequestBody final Map<String, Object> record) {
		service.send(record);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
