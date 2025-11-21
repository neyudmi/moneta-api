package com.example.expense_service.dtos;

import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreateCategoryDTO {

    private String name;
    private UUID iconId;
    private UUID groupId;
    private UUID userId;
}