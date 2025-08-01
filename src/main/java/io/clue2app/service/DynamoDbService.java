package io.clue2app.service;

import io.clue2solve.parameters.C2aConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "clue2app.dynamodb.enabled", havingValue = "true")
public class DynamoDbService {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDbService.class);

    @Autowired
    private C2aConfig clue2appConfig;

    @Autowired
    private DynamoDbClient client;

    @Value("${clue2app.dynamodb.name}")
    private String dynamodbConfigName;

    private String tableName;

    public DynamoDbService() {
        logger.debug("Configuring DynamoDB with config name: {}", dynamodbConfigName);

        final Map<String, String> config = clue2appConfig.getSection(dynamodbConfigName);

        if (config == null || config.isEmpty()) {
            logger.error("DynamoDB configuration for '{}' is missing or empty", dynamodbConfigName);
            throw new IllegalStateException("DynamoDB configuration is not properly set up");
        }

        tableName = config.get("name");
    }

    public Object create(final Map<String, Object> payload) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.fromS(payload.get("id").toString()));
        item.put("firstName", AttributeValue.fromS(payload.get("firstName").toString()));
        item.put("lastName", AttributeValue.fromS(payload.get("lastName").toString()));
        item.put("email", AttributeValue.fromS(payload.get("email").toString()));

        return client.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());
    }

    public Object get(final String id) {
        final Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.fromS(id));

        final GetItemResponse response = client.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build());

        return Map.of(
            "id", response.item().get("id").s(),
            "firstName", response.item().get("firstName").s(),
            "lastName", response.item().get("lastName").s(),
            "email", response.item().get("email").s()
        );
    }

    public void delete(final String id) {
        final Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.fromS(id));

        client.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build());

        logger.debug("Item with id {} deleted from DynamoDB table {}", id, tableName);
    }

}
