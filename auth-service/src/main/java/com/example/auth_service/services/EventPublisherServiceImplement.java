package com.example.auth_service.services;

import com.example.auth_service.configs.RabbitMQConfig;
import com.example.auth_service.events.UserCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherServiceImplement implements EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public EventPublisherServiceImplement(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishUserCreatedEvent(UserCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                RabbitMQConfig.USER_CREATED_ROUTING_KEY,
                event
        );
    }
} 