package io.clue2app.service;

import io.clue2solve.parameters.C2aConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    private String bucketName;
    private S3Client s3Client;
    private C2aConfig clue2appConfig;

    public S3Service(@Autowired S3Client s3Client,
                     @Autowired C2aConfig clue2appConfig,
                     @Value("${clue2app.s3.name}") String bucketConfigName) {
        this.s3Client = s3Client;
        this.clue2appConfig = clue2appConfig;

        final Map<String, String> bucketConfig = clue2appConfig.getSection(bucketConfigName);

        if (bucketConfig == null || bucketConfig.isEmpty()) {
            logger.error("S3 bucket configuration for '{}' is missing or empty", bucketConfigName);
            throw new IllegalStateException("Queue configuration is not properly set up");
        }

        this.bucketName = bucketConfig.get("name");
    }

    public void uploadFile(final MultipartFile file) throws IOException {
        logger.debug("Uploading file to S3: key={}", file.getOriginalFilename());

        final String fileName = file.getOriginalFilename().toLowerCase().replace(" ", "_");

        final PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        logger.info("Uploaded file to S3: key={}", fileName);
    }

    public Path downloadFile(final String key) throws IOException {
        logger.debug("Downloading file from S3: key={}", key);

        final GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        final Path tempFile = Paths.get(String.format("%s%s%s", System.getProperty("java.io.tmpdir"), FileSystems.getDefault().getSeparator(), key));

        final ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request);
        Files.copy(s3Object, tempFile, StandardCopyOption.REPLACE_EXISTING);

        return tempFile;
    }

    public List<String> listFiles() {
        logger.debug("Listing files in S3 bucket: {}", bucketName);

        final ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        final ListObjectsV2Response result = s3Client.listObjectsV2(request);

        return result.contents().stream().map(S3Object::key).collect(Collectors.toList());
    }

    public void deleteFile(final String key) {
        logger.debug("Deleting file from S3: key={}", key);

        final DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);

        logger.info("Deleted file from S3: key={}", key);
    }
}
