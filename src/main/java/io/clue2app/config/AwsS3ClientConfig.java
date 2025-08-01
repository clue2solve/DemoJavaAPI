package io.clue2app.config;

import io.clue2solve.parameters.C2aConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "clue2app.s3.enabled", havingValue = "true")
public class AwsS3ClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(AwsS3ClientConfig.class);

    @Autowired
    private C2aConfig clue2appConfig;

    @Value("${clue2app.s3.name}")
    private String bucketConfigName;

    @Bean
    public S3Client s3Client() {
        logger.debug("Configuring S3Client with bucket name: {}", bucketConfigName);

        final Map<String, String> config = clue2appConfig.getSection(bucketConfigName);

        if (config == null || config.isEmpty()) {
            logger.error("S3 bucket configuration for '{}' is missing or empty", bucketConfigName);
            throw new IllegalStateException("Bucket configuration is not properly set up");
        }

        final String region = config.get("region");

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.get("accessKey"), config.get("secretKey")))
                )
                .build();
    }
}