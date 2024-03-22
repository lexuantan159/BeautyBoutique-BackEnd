package com.example.beautyboutique.Services;

import com.example.beautyboutique.DTOs.*;
import com.example.beautyboutique.Exceptions.DataNotFoundException;
import com.example.beautyboutique.Models.User;

public interface AuthenticationService {
    User signup(SignUpRequest signUpRequest)throws DataNotFoundException;
    JwtAuthenticationResponse signin(SignInRequest signInRequest);
    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

}
