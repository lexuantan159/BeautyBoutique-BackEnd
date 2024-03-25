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

    public  String sendEmail(String email,String subject,String body){
        SimpleMailMessage message =new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("hnphong37m1@gmail.com");
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        return "send ok";
    }


}
