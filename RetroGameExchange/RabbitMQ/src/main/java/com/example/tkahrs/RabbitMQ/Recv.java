package com.example.tkahrs.RabbitMQ;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Recv {

//    <dependency>
//			<groupId>com.rabbitmq</groupId>
//			<artifactId>amqp-client</artifactId>
//			<version>5.14.2</version>
//		</dependency>

    private final static String QUEUE_NAME = "email_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(9001);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8); // the message that will be received
//            formatEmail(message);
            System.out.println(message);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

    private static void formatEmail(String email) { // put the object here
        System.out.println(" [x] Received '" + email + "'");
        Map<String, String> emailInfo = new HashMap<String, String>();
        String[] pairs = email.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            emailInfo.put(keyValue[0], keyValue[1]);
        }
        new SendMail(emailInfo.get("address"), emailInfo.get("subject"), emailInfo.get("body")); // sending the email
    }
}