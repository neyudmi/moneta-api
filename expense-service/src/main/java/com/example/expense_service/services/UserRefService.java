package com.example.expense_service.services;

import com.example.expense_service.events.UserCreatedEvent;
import com.example.expense_service.models.UserRef;
import com.example.expense_service.repositories.UserRefRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRefService {

    private final UserRefRepository userRefRepository;

    public UserRefService(UserRefRepository userRefRepository) {
        this.userRefRepository = userRefRepository;
    }

    @Transactional
    public void handleUserCreated(UserCreatedEvent event) {
        if (userRefRepository.existsById(event.getUserId())) {
            System.out.println("⚠️ UserRef already exists: " + event.getEmail());
            return;
        }

        UserRef userRef = new UserRef(
                event.getUserId(),
                event.getUsername(),
                event.getEmail());

        userRefRepository.save(userRef);
        System.out.println("✅ [EXPENSE-SERVICE] Saved user_ref: " + event.getEmail());
    }
}
