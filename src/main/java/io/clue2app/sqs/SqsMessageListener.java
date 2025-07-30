package io.clue2app.sqs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsMessageListener {

    @Value("${clue2app.queue.name}")
    private String queueName;

    @SqsListener("#{SqsMessageListener.queueName}")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }
}
