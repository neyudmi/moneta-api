package com.example.expense_service.events;

import com.example.expense_service.configs.RabbitMQConfig;
import com.example.expense_service.models.Category;
import com.example.expense_service.services.UserRefService;
import com.example.expense_service.repositories.CategoryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UserCreatedListener {

    private final UserRefService userRefService;
    private final CategoryRepository categoryRepository;

    public UserCreatedListener(UserRefService userRefService, CategoryRepository categoryRepository) {
        this.userRefService = userRefService;
        this.categoryRepository = categoryRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void onUserCreated(UserCreatedEvent event) {
        System.out.println("[EXPENSE-SERVICE] Received UserCreatedEvent: " + event.getEmail());

        userRefService.handleUserCreated(event);

        UUID newUserId = event.getUserId();
        List<Category> defaults = categoryRepository.findByUserId(null);
        List<Category> clones = new ArrayList<>();

        for (Category c : defaults) {
            Category clone = new Category();
            clone.setName(c.getName());
            clone.setGroup(c.getGroup());
            clone.setIcon(c.getIcon());
            clone.setUserId(newUserId);
            clones.add(clone);
        }

        categoryRepository.saveAll(clones);
        System.out.println("Initialized " + clones.size() + " categories for new user: " + event.getUsername());
    }
}
