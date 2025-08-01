package io.clue2app.config;

import io.clue2solve.parameters.C2aConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "clue2app.dynamodb.enabled", havingValue = "true")
public class AwsDynamoDbConfig {

    private static final Logger logger = LoggerFactory.getLogger(AwsDynamoDbConfig.class);

    @Autowired
    private C2aConfig clue2appConfig;

    @Value("${clue2app.dynamodb.name}")
    private String dynamodbConfigName;

    @Bean
    public DynamoDbClient dynamoDbClient () {
        logger.debug("Configuring DynamoDB with config name: {}", dynamodbConfigName);

        final Map<String, String> config = clue2appConfig.getSection(dynamodbConfigName);

        if (config == null || config.isEmpty()) {
            logger.error("DynamoDB configuration for '{}' is missing or empty", dynamodbConfigName);
            throw new IllegalStateException("DynamoDB configuration is not properly set up");
        }

        final String region = config.get("region");

        return DynamoDbClient.builder()
                .region(Region.of(region))
                //.endpointOverride(URI.create("http://localhost:8000")) // if using local DynamoDB
                .build();
    }

}