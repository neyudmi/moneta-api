package com.example.expense_service.events;

import com.example.expense_service.configs.RabbitMQConfig;
import com.example.expense_service.services.UserRefService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedListener {

    private final UserRefService userRefService;

    public UserCreatedListener(UserRefService userRefService) {
        this.userRefService = userRefService;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void onUserCreated(UserCreatedEvent event) {
        System.out.println("📩 [EXPENSE-SERVICE] Received UserCreatedEvent: " + event.getEmail());
        userRefService.handleUserCreated(event);
    }
}
