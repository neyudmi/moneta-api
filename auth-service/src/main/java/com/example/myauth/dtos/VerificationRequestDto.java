package com.example.myauth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "\\d{6}")
    private String code;
}
