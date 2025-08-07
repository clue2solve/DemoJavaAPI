package io.clue2app.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import io.clue2solve.parameters.C2aConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "clue2app.queue.enabled", havingValue = "true")
public class AwsQueueClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(AwsQueueClientConfig.class);

    @Autowired
    private C2aConfig clue2appConfig;

    @Autowired
    private WebIdentityTokenCredentialsProvider webIdentityTokenCredentialsProvider;

    @Value("${clue2app.queue.name}")
    private String queueConfigName;

    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        logger.debug("Configuring AmazonSQSAsync with queue name: {}", queueConfigName);

        final Map<String, String> config = clue2appConfig.getSection(queueConfigName);

        if (config == null || config.isEmpty()) {
            logger.error("Queue configuration for '{}' is missing or empty", queueConfigName);
            throw new IllegalStateException("Queue configuration is not properly set up");
        }

        final String region = config.get("region");

        return AmazonSQSAsyncClientBuilder.standard()
                .withRegion(region == null || region.isEmpty() ? "us-west-2" : region)
                .withCredentials(
                        webIdentityTokenCredentialsProvider
//                    new DefaultAWSCredentialsProviderChain()
//                    new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.get("accessKey"), config.get("secretKey")))
                )
                .build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(final AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }


}