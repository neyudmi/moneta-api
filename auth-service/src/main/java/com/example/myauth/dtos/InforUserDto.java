package com.example.myauth.dtos;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InforUserDto {
    private String fullName;
    private String email;
    private LocalDate birthDay;
    private String gender;
}
