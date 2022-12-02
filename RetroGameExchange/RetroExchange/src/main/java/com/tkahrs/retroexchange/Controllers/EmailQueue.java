package com.tkahrs.retroexchange.Controllers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;

public class EmailQueue {
    private final static String QUEUE_NAME = "email_queue";

//    @Value("${RABBIT_NAME:localhost}")
    String hostname = "some-rabbit";
//    @Value("${RABBIT_PORT:9001}")
    int rabbitPort = 5672;

    public EmailQueue(String params) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        factory.setPort(rabbitPort);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            channel.basicPublish("", QUEUE_NAME, null, params.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + params + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
