package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.JwtAuthenticationResponse;
import com.example.beautyboutique.DTOs.RefreshTokenRequest;
import com.example.beautyboutique.DTOs.SignInRequest;
import com.example.beautyboutique.DTOs.SignUpRequest;
import com.example.beautyboutique.Exceptions.DataNotFoundException;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Services.AuthenticationService;
import com.example.beautyboutique.Services.EmailService;
import com.example.beautyboutique.Services.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    private final UserService userService;

    private final EmailService mailService;
    @PostMapping("/register")
    public ResponseEntity <?> signup(@RequestBody SignUpRequest signUpRequest){
        try {
            if(!signUpRequest.getPassword().equals(signUpRequest.getRetypePassword())){
                return ResponseEntity.badRequest().body("pass word does not match");
            }
            User user =  authenticationService.signup(signUpRequest);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> siginin(
            @RequestBody SignInRequest signInRequest){
        try {
            JwtAuthenticationResponse authenticationResponse = authenticationService.signin(signInRequest);
            return ResponseEntity.ok(authenticationResponse);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(
            @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }
    @PostMapping("/sendotp")
    public ResponseEntity<String> otp(
            @RequestParam(name = "username") String username)
    {
        String subject= "Ma OTP CUA BAN:";
        String OTP =   authenticationService.generateRandomOTP();
        String email = authenticationService.getEmail(username);
        return  ResponseEntity.ok(mailService.sendEmail(email,subject,OTP));
    }
    @PostMapping("/resetpass")
    public ResponseEntity<String> resetpass(
            @RequestParam(name = "username") String username)
    {
        String subject= "Mat khau moi cua ban la:";
        String newpass=   authenticationService.resetpass(username);
        String email = authenticationService.getEmail(username);
        return  ResponseEntity.ok(mailService.sendEmail(email,subject,newpass));
    }

}
