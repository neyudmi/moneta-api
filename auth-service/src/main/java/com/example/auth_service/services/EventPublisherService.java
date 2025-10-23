package com.example.auth_service.services;

import com.example.auth_service.events.UserCreatedEvent;

public interface EventPublisherService {
    void publishUserCreatedEvent(UserCreatedEvent event);
} 