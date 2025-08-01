package io.clue2app.controller;

import io.clue2app.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@ConditionalOnProperty(name = "clue2app.s3.enabled", havingValue = "true")
public class S3Controller {

	private final Logger logger = LoggerFactory.getLogger(S3Controller.class);

	static S3Service service;

	public S3Controller(@Autowired S3Service s3Service) {
		service = s3Service;
	}

	@GetMapping("/s3")
	public ResponseEntity<List<String>> listFiles() {
		return new ResponseEntity<>(service.listFiles(), HttpStatus.OK);
	}

	@GetMapping("/s3/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") final String fileName) throws IOException {
		final Path filePath = service.downloadFile(fileName);
		final Resource resource = new UrlResource(filePath.toUri());

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}

	@PostMapping("/s3")
	ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		service.uploadFile(file);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/s3/{fileName}")
	ResponseEntity<Object> deleteFile(@PathVariable("fileName") final String fileName) {
		service.deleteFile(fileName);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
