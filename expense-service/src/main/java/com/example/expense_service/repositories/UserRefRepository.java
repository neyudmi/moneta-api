package com.example.expense_service.repositories;

import com.example.expense_service.models.UserRef;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRefRepository extends JpaRepository<UserRef, UUID> {
}
