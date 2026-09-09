package com.example.myauth.events;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.myauth.entities.User;
import com.example.myauth.services.EmailService;
import com.example.myauth.services.VerificationService;
import com.example.myauth.utils.RandomVerificationCode;

@Component
// Lắng nghe RegistractionEvent
// Tạo token --> lưu vào database --> gửi email xác nhận
public class RegistrationListener {
    private final VerificationService verificationService; // Service để tạo token và lưu vào database
    private final EmailService emailService; // Service để gửi email

    public RegistrationListener(VerificationService verificationService, EmailService emailService) {
        this.verificationService = verificationService;
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void confirmRegistration(RegistracionEvent event) {

        User user = event.getUser();

        String code = RandomVerificationCode.generateCode();

        // Lưu code vào Redis
        verificationService.createVerificationCode(user.getEmail(), code);

        // Gửi email
        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getFullName(),
                code);
    }
}
