package com.example.auth_service.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ResetPasswordDto {
    private String email;
    private String newPassword;
}
