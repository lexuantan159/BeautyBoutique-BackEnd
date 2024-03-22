package com.example.beautyboutique.Services;

import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.UserRepository;
import com.example.beautyboutique.Services.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public  String sendEmail(String username){
        String subject = "Khôi phục mật khẩu";
        Optional<User> Opuser = userRepository.findByUsername(username);
        User user = Opuser.orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        String email= user.getEmail();
        if (email == null) {
            throw new IllegalArgumentException("user không tồn tại");
        }
        String newPassword = generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        String body = "Mật khẩu mới của bạn là: " + newPassword;
        SimpleMailMessage message =new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("hnphong37m1@gmail.com");
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        return "send ok";
    }
    private String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

}
