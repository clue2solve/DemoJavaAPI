package io.clue2app.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
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

import java.util.Map;

@Configuration
public class AwsSqsClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(AwsSqsClientConfig.class);

    @Autowired
    private C2aConfig clue2appConfig;

    @Value("${clue2app.queue.name}")
    private String queueName;

    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        logger.debug("Configuring AmazonSQSAsync with queue name: {}", queueName);

        final Map<String, String> queueConfig = clue2appConfig.getSection(queueName);

        if (queueConfig == null || queueConfig.isEmpty()) {
            logger.error("Queue configuration for '{}' is missing or empty", queueName);
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
}