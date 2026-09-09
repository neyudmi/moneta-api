package com.example.myauth.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank
    @Size(max = 100)
    private String fullName;
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
    @NotBlank
    @Size(min = 8, max = 72)
    private String password;
    @NotNull
    private LocalDate birthDay;
    @NotBlank
    @Size(max = 10)
    private String gender;

}
