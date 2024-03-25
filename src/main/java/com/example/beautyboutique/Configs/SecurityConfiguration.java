package com.example.beautyboutique.Configs;

import com.example.beautyboutique.Services.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests ->

                        requests
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/cart/**").permitAll()
                                .requestMatchers("/api/blog/**").permitAll()
                                .requestMatchers("/api/voucher/**").permitAll()
                                .requestMatchers("/api/order/get-all-orders").hasAnyRole("ADMIN")
                                .requestMatchers("/api/order/get-summary-orders").hasAnyRole("ADMIN")
                                .requestMatchers("/api/order/order-histories").hasAnyRole("USER")
                                .requestMatchers("/api/order/create-payment").hasAnyRole("USER")
                                .requestMatchers("/api/order/create-order").hasAnyRole("USER")
                                .requestMatchers("/api/order/update-order").hasAnyRole("USER")
                                .requestMatchers("/api/order/cancel-order").hasAnyRole("USER")
                                .requestMatchers("/api/order/approve-order").hasAnyRole("ADMIN")
                                .requestMatchers("/api/order/change-status").hasAnyRole("ADMIN")
                                .requestMatchers("/api/ship-detail/**").permitAll()
                                .requestMatchers("/api/auth/login").permitAll()
                                .requestMatchers("/api/users/deleteUser").hasAnyRole("ADMIN")
                                .requestMatchers("/api/auth/register").permitAll()
                                .requestMatchers("/api/auth/forgot").permitAll()
                                .requestMatchers("/api/voucher/**").permitAll()
                                .requestMatchers("/api/product/**").permitAll()
                                .requestMatchers("/api/category/**").permitAll()
                                .requestMatchers("/api/product/**").permitAll()
                                .requestMatchers("/api/category/**").permitAll()
                                .requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/blog/comment/**").permitAll()
                                .requestMatchers("/api/product/feedback/**").permitAll()
                                .requestMatchers("/api/voucher/create-voucher").hasRole("ADMIN")
                                .requestMatchers("/api/voucher/delete-voucher").hasRole("ADMIN")
                                .requestMatchers("/api/voucher/update-voucher").hasRole("ADMIN")
                                .requestMatchers("/api/voucher/get-all-voucher").permitAll()
                                .requestMatchers("/api/order/**").permitAll()
                                .requestMatchers("/api/ship-detail/**").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                );
        return http.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

}
