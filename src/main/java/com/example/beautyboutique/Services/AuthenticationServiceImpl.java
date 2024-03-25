package com.example.beautyboutique.Services;

import com.example.beautyboutique.DTOs.*;
import com.example.beautyboutique.Exceptions.DataNotFoundException;
import com.example.beautyboutique.Models.Role;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl  implements AuthenticationService{
    private final UserRepository    userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
//    private final JavaMailSender mailSender;
    @Override
    public User signup(SignUpRequest signUpRequest) {
            String username =signUpRequest.getUsername();
            if(userRepository.existsByUsername(username)){
                throw new DataIntegrityViolationException("username already exists");
            }
            String email=signUpRequest.getEmail();
            if(userRepository.existsByEmail(email)){
                throw new DataIntegrityViolationException("email already exists");
            }

            User user = new User();
            user.setFullName("");
            user.setUsername(username);
            user.setEmail(email);
            user.setAddress("");
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setDateOfBirth(new Date(System.currentTimeMillis()));
            Role role = new Role();
            role.setRoleId(2);
            role.setRoleName("USER");
            user.setRole(role);
            return userRepository.save(user);
    }
    public JwtAuthenticationResponse signin(SignInRequest signInRequest) throws Exception{
        String username =signInRequest.getUsername();
        if(username.isEmpty()) {
            throw new DataNotFoundException("INVALID User");
        }
        if(!userRepository.existsByUsername(username)){
            throw new DataIntegrityViolationException("username does not exist");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                (signInRequest.getUsername(),signInRequest.getPassword()));
        var user =userRepository.findByUsername(signInRequest.getUsername()).
                orElseThrow(()->new IllegalArgumentException("INVALID USERNAME OR PASSWORD"));
        var jwt = jwtService.generateToken(user);
        var refreshtoken = jwtService.generateRefreshToken(new HashMap<>(),user);
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshtoken(refreshtoken);
        return  jwtAuthenticationResponse;
    }
    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
        String username = jwtService.extractUserName(refreshTokenRequest.getToken());
        User user = userRepository.findByUsername(username).orElseThrow();
        if(jwtService.isTokenValid(refreshTokenRequest.getToken(), user))
        {
            var jwt =jwtService.generateToken(user);
            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshtoken(refreshTokenRequest.getToken());
            return  jwtAuthenticationResponse;
        }
        return null;
    }
    public String resetpass(String username){
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
        return  newPassword;
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
    public String getEmail(String username) {
        Optional<User> Opuser = userRepository.findByUsername(username);
        User user = Opuser.orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        String email= user.getEmail();
        return email;
    }



}
