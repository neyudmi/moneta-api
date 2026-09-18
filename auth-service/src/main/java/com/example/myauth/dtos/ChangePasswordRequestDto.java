package com.example.myauth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {
    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String currentPassword;
    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;

}
