package com.example.beautyboutique.Services;

import com.example.beautyboutique.Models.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

public interface JWTService {
    String extractUserName(String token);
    String generateToken(UserDetails userDetails);
    boolean isTokenValid(String token ,UserDetails userDetails);
    String generateRefreshToken(Map<String,Object> extraClaims, UserDetails userDetails);

    Integer getUserIdByToken(HttpServletRequest request);

}
