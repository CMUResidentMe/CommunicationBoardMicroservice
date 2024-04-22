package com.example.communicationboard.kafka;

import com.example.communicationboard.dto.RmNotification;
import com.example.communicationboard.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MsgProducer {
    private static final Logger logger = LoggerFactory.getLogger(MsgProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // Kafka topic name
    @Value("${communicationBoard.kafkaTopic}")
    private String kafkaTopic;

    // Kafka event names
    @Value("${communicationBoard.threadDeleted}")
    private String threadDeletedEvent;

    @Value("${communicationBoard.postCreated}")
    private String postCreatedEvent;

    @Value("${communicationBoard.postDeleted}")
    private String postDeletedEvent;

    @Value("${communicationBoard.replyCreated}")
    private String replyCreatedEvent;

    @Value("${communicationBoard.replyDeleted}")
    private String replyDeletedEvent;

    // Methods to send notifications to Kafka
    public void sendThreadDeletedNotification(RmNotification notification) {
        String message = JsonUtil.convert2Str(notification);
        kafkaTemplate.send(kafkaTopic, threadDeletedEvent, message);
        logger.info("Sent Thread Deleted notification: {}", message);
    }

    public void sendPostCreatedNotification(RmNotification notification) {
        String message = JsonUtil.convert2Str(notification);
        kafkaTemplate.send(kafkaTopic, postCreatedEvent, message);

        logger.info("Sent Post Created notification: {}", message);
    }

    public void sendPostDeletedNotification(RmNotification notification) {
        String message = JsonUtil.convert2Str(notification);
        kafkaTemplate.send(kafkaTopic, postDeletedEvent, message);
        logger.info("Sent Post Deleted notification: {}", message);
    }

    public void sendReplyCreatedNotification(RmNotification notification) {
        String message = JsonUtil.convert2Str(notification);
        kafkaTemplate.send(kafkaTopic, replyCreatedEvent, message);
        logger.info("Sent Reply Created notification: {}", message);
    }

    public void sendReplyDeletedNotification(RmNotification notification) {
        String message = JsonUtil.convert2Str(notification);
        kafkaTemplate.send(kafkaTopic, replyDeletedEvent, message);
        logger.info("Sent Reply Deleted notification: {}", message);
    }
}
