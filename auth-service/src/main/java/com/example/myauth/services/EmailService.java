package com.example.myauth.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.myauth.exceptions.EmailSendingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(
            String email,
            String fullName,
            String verificationCode) {

        Context context = new Context();

        context.setVariable("fullName", fullName);
        context.setVariable("verificationCode", verificationCode);

        String htmlMessage = templateEngine.process("verify-email", context);

        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Xác minh tài khoản Moneta");
            helper.setText(htmlMessage, true);

            emailSender.send(message);

        } catch (MessagingException | RuntimeException e) {
            throw new EmailSendingException("Failed to send verification email.", e);
        }
    }

}
