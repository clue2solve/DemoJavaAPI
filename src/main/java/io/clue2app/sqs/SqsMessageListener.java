package io.clue2app.sqs;

import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

public class SqsMessageListener {

    @SqsListener("your-queue-name")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }
}
