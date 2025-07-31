package io.clue2app.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import io.clue2solve.parameters.C2aConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Map;

@Configuration
public class AwsClientsConfig {

    private static final Logger logger = LoggerFactory.getLogger(AwsClientsConfig.class);

    @Autowired
    private C2aConfig clue2appConfig;

    @Value("${clue2app.queue.name}")
    private String queueConfigName;

    @Value("${clue2app.s3.name}")
    private String bucketConfigName;

    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        logger.debug("Configuring AmazonSQSAsync with queue name: {}", queueConfigName);

        final Map<String, String> queueConfig = clue2appConfig.getSection(queueConfigName);

        if (queueConfig == null || queueConfig.isEmpty()) {
            logger.error("Queue configuration for '{}' is missing or empty", queueConfigName);
            throw new IllegalStateException("Queue configuration is not properly set up");
        }

        final String region = queueConfig.get("region");

        return AmazonSQSAsyncClientBuilder.standard()
                .withRegion(region == null || region.isEmpty() ? "us-west-2" : region)
                .withCredentials(
/*
                    new DefaultAWSCredentialsProviderChain()
*/
                    new AWSStaticCredentialsProvider(new BasicAWSCredentials(queueConfig.get("accessKey"), queueConfig.get("secretKey")))
                )
                .build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(final AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }

    @Bean
    public S3Client s3Client() {
        logger.debug("Configuring S3Client with bucket name: {}", bucketConfigName);

        final Map<String, String> bucketConfig = clue2appConfig.getSection(bucketConfigName);

        if (bucketConfig == null || bucketConfig.isEmpty()) {
            logger.error("S3 bucket configuration for '{}' is missing or empty", bucketConfigName);
            throw new IllegalStateException("Bucket configuration is not properly set up");
        }

        final String region = bucketConfig.get("region");

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(bucketConfig.get("accessKey"), bucketConfig.get("secretKey")))
                )
                .build();
    }
}