package com.example.tkahrs.RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

public class Send {

    private final static String QUEUE_NAME = "email_queue";
    static String email = "address:cstanley@student.neumont.edu,subject:It Works!,body:You big brained genius";

//    <dependency>
//			<groupId>com.rabbitmq</groupId>
//			<artifactId>amqp-client</artifactId>
//			<version>5.14.2</version>
//		</dependency>

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(9001);
        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            channel.basicPublish("", QUEUE_NAME, null, email.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + email + "'");
        }
    }
}