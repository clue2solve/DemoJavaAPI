package io.clue2app.service;

import io.clue2solve.parameters.C2aConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnProperty(name = "clue2app.queue.enabled", havingValue = "true")
public class SqsService {

    private static final Logger logger = LoggerFactory.getLogger(SqsService.class);

    private final QueueMessagingTemplate queueMessagingTemplate;

    private final String queueName;

    public SqsService(
            @Autowired QueueMessagingTemplate queueMessagingTemplate,
            @Autowired final C2aConfig config,
            @Value("${clue2app.queue.name}") final String queueConfigName
    ) {
        this.queueMessagingTemplate = queueMessagingTemplate;

        final Map<String, String> queueConfig = config.getSection(queueConfigName);

        if (queueConfig == null || queueConfig.isEmpty()) {
            logger.error("Queue configuration for '{}' is missing or empty", queueConfigName);
            throw new IllegalStateException("Queue configuration is not properly set up");
        }

        queueName = queueConfig.get("name");

        if (queueName == null || queueName.isEmpty()) {
            logger.error("Queue name is not specified in the configuration for '{}'", queueConfigName);
            throw new IllegalStateException("Queue name is not properly set up");
        }
    }

    public Object getOne() {
        logger.debug("Reading messages from SQS queue: {}", queueName);

        final Message<?> message = queueMessagingTemplate.receive(queueName);

        logger.debug("Received message from SQS queue: {}", message);

        return message != null ? message.getPayload() : null;
    }

    public void send(final Map<String, Object> message) {
        queueMessagingTemplate.send(queueName, MessageBuilder.withPayload(message).build());

        logger.debug("Sent message to SQS: {}", message);
    }
}
