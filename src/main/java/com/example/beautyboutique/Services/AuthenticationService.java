package com.example.beautyboutique.Services;

import com.example.beautyboutique.DTOs.*;
import com.example.beautyboutique.Exception.ResourceNotFoundException;
import com.example.beautyboutique.Exceptions.DataNotFoundException;
import com.example.beautyboutique.Models.User;

public interface AuthenticationService {
    User signup(SignUpRequest signUpRequest);
    JwtAuthenticationResponse signin(SignInRequest signInRequest) throws Exception;
    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    String resetpass(String username);
    public String generateRandomOTP();
    String getEmail(String username);

    User ChangePassWord(Integer id, String oldPassword, String newPassword) throws ResourceNotFoundException;
}
