package com.example.beautyboutique.Services;

import com.example.beautyboutique.Exceptions.InvalidParamException;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.awt.image.TileObserver;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {
    @Autowired
    UserRepository userRepository;
    @Value("${jwt.secretKey}")
    private String secretKey;

    public JWTServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 2))
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(getSiginKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Integer getUserIdByToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            // Remove "Bearer " prefix
            token = token.substring(7);
            String userName = extractClaim(token, Claims::getSubject);
            Optional<User> userOptional = userRepository.findByUsername(userName);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return user.getId();
            }
        }
        return -1;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSiginKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSiginKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}
