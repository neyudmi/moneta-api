package com.example.auth_service.dtos;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class UserInfoResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private String fullName;
    private String email;
    private LocalDate birthDay;
    private String gender;

    public UserInfoResponseDto(UUID id, String fullName, String email, LocalDate birthDay, String gender) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.birthDay = birthDay;
        this.gender = gender;
    }

    public UserInfoResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
