package com.example.myauth.events;

import org.springframework.context.ApplicationEvent;

import com.example.myauth.entities.User;

// Sử dụng để thông báo rằng việc đăng ký User đã hoàn tất
public class RegistracionEvent extends ApplicationEvent { // đại diện cho một event xảy ra trong application
                                                          // OnRegistrationCompleteEvent là một event riêng

    private final User user; // mang theo User vừa đăng ký -> truyền cho listener

    public RegistracionEvent(User user) {
        super(user); // gọi constructor ApplicationEvent
        this.user = user; // lưu User vào field của Event
    }

    // Cho listener có thể lấy được User vừa đăng ký
    public User getUser() {
        return user;
    }
}