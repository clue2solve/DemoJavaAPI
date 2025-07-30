package io.clue2app.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "clue2app.queue.enabled", havingValue = "true")
public class SqsScheduler {
    private static final Logger logger = LoggerFactory.getLogger(SqsScheduler.class);

    @Autowired
    private SqsMessageSender messageSender;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Faker faker;

    @Value("${clue2app.queue.name}")
    private String queueName;

    @Scheduled(fixedDelay = 60000)
    public void sendTestMessageEvery1Min() {
        logger.debug("Scheduler: Sending test message to SQS queue [queueName: {}])", queueName);

        final Map<String, Object> payload = Map.of(
                "firstName", faker.name().firstName(),
                "lastName", faker.name().lastName(),
                "birthDate", faker.date().birthday(),
                "email", faker.internet().emailAddress(),
                "phoneNumber", faker.phoneNumber().phoneNumber(),
                "address", faker.address().fullAddress(),
                "company", faker.company().name(),
                "jobTitle", faker.job().title(),
                "message", faker.lorem().sentence()
        );

        try {
            messageSender.send(mapper.writeValueAsString(payload));
        } catch (final Exception e) {
            logger.error("Error serializing payload: {}", e.getMessage(), e);
        }
    }
}
