package com.example.auth_service.services;

import com.example.auth_service.dtos.LoginUserDto;
import com.example.auth_service.dtos.RegisterUserDto;
import com.example.auth_service.dtos.VerifyUserDto;
import com.example.auth_service.dtos.ResetPasswordDto;
import com.example.auth_service.models.User;
import com.example.auth_service.events.UserCreatedEvent;
import com.example.auth_service.repositories.UserRepository;

import jakarta.mail.MessagingException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.DisabledException; // Thêm import này

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final EventPublisherService eventPublisherService;
    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            EventPublisherService eventPublisherService,
            EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.eventPublisherService = eventPublisherService;
        this.emailService = emailService;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setGender(input.getGender());
        user.setBirthDay(input.getBirthDay());
        user.setPassword(input.getPassword());
        user.setPasswordLastChanged(LocalDateTime.now());
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);

        User savedUser = userRepository.save(user);

        // Publish user created event
        UserCreatedEvent event = new UserCreatedEvent(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail());
        eventPublisherService.publishUserCreatedEvent(event);

        return savedUser;
    }

    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isEnabled()) {
            throw new RuntimeException("Account is not verified. Please check your email for the verification code.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()));

        return user;
    }

    public User loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(User user) {
        String subject = "Xác minh tài khoản Moneta";
        String verificationCode = user.getVerificationCode();

        String htmlMessage = """
                <html>
                <body style="font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 0;">
                    <div style="max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden;">

                        <!-- Header -->
                        <div style="background-color: #007bff; padding: 20px 0; text-align: center;">
                            <img src="https://i.ibb.co/XfJhy8Yx/logo.png" alt="Moneta Logo" style="height: 60px;"/>
                            <h2 style="color: #ffffff; margin: 10px 0 0; font-weight: 600;">Chào mừng bạn đến với Moneta!</h2>
                        </div>

                        <!-- Nội dung -->
                        <div style="padding: 30px 40px; color: #333333;">
                            <p style="font-size: 16px;">Xin chào <strong>%s</strong>,</p>
                            <p style="font-size: 16px;">
                                Cảm ơn bạn đã đăng ký tài khoản Moneta.
                                Để hoàn tất quá trình đăng ký, vui lòng nhập mã xác minh bên dưới:
                            </p>

                            <!-- Mã xác minh -->
                            <div style="background-color: #f1f5ff; border: 2px dashed #007bff; border-radius: 8px; text-align: center; padding: 20px; margin: 25px 0;">
                                <p style="font-size: 22px; letter-spacing: 4px; color: #007bff; font-weight: bold;">%s</p>
                            </div>

                            <p style="font-size: 15px; color: #666;">
                                Mã xác minh có hiệu lực trong <strong>15 phút</strong>.
                                Nếu bạn không thực hiện yêu cầu, vui lòng bỏ qua email này.
                            </p>

                            <p style="font-size: 15px;">Trân trọng,<br/>Đội ngũ <strong>Moneta</strong></p>
                        </div>

                        <!-- Footer -->
                        <div style="background-color: #f0f0f0; text-align: center; padding: 15px; font-size: 13px; color: #777;">
                            © 2025 Moneta.<br/>
                            <a href="https://moneta.vn" style="color: #007bff; text-decoration: none;">MONETA</a>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(user.getFullName(), verificationCode);

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // (1) Gửi mã OTP đặt lại mật khẩu
    public void forgotPassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        String code = generateVerificationCode();
        user.setVerificationCode(code);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String subject = "Xác nhận đặt lại mật khẩu - Moneta";
        String htmlMessage = """
                <html><body style="font-family: Arial, sans-serif; background-color:#f4f6f8;">
                <div style="max-width:600px;margin:20px auto;background-color:white;padding:30px;border-radius:10px;">
                    <h2 style="color:#FF5722;">Xin chào %s,</h2>
                    <p>Bạn vừa yêu cầu đặt lại mật khẩu tài khoản Moneta.</p>
                    <p>Nhập mã OTP sau để xác nhận:</p>
                    <h1 style="text-align:center;letter-spacing:4px;color:#FF5722;">%s</h1>
                    <p>Mã hết hạn trong <strong>15 phút</strong>.</p>
                </div></body></html>
                """.formatted(user.getFullName(), code);

        try {
            emailService.sendVerificationEmail(email, subject, htmlMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset email");
        }
    }

    // (2) Xác minh mã OTP đặt lại mật khẩu
    public void verifyResetCode(String email, String verificationCode) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        if (user.getVerificationCodeExpiresAt() == null ||
                user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code expired");
        }

        if (!user.getVerificationCode().equals(verificationCode)) {
            throw new RuntimeException("Invalid verification code");
        }

        // Đánh dấu trạng thái cho phép đổi mật khẩu (nếu bạn có cờ riêng)
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        user.setEnabled(true); // chỉ tạm bật quyền đổi mật khẩu
        userRepository.save(user);
    }

    // (3) Đổi mật khẩu sau khi xác minh thành công
    public void resetPassword(String email, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        if (!user.isEnabled()) { // hoặc kiểm tra flag riêng nếu có
            throw new RuntimeException("Please verify OTP first");
        }

        // 🔒 Nếu có BCrypt:
        // user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(newPassword);
        user.setPasswordLastChanged(LocalDateTime.now());
        userRepository.save(user);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
