package io.clue2app.sqs;

import io.clue2solve.parameters.C2aConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SqsMessageSender {

    private static final Logger logger = LoggerFactory.getLogger(SqsMessageSender.class);

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private C2aConfig clue2appConfig;

    @Value("${clue2app.queue.name}")
    private String queueName;

    public void send(String message) {
        final Map<String, String> queueConfig = clue2appConfig.getSection(queueName);

        if (queueConfig == null || queueConfig.isEmpty()) {
            logger.error("Queue configuration for '{}' is missing or empty", queueName);
            throw new IllegalStateException("Queue configuration is not properly set up");
        }

        final String queueName = queueConfig.get("name");

        queueMessagingTemplate.send(queueName, MessageBuilder.withPayload(message).build());
    }
}