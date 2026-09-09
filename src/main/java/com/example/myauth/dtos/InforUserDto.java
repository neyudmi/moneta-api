package com.example.myauth.dtos;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InforUserDto {
    private UUID id;
    private String fullName;
    private String email;
    private LocalDate birthDay;
    private String gender;
}
