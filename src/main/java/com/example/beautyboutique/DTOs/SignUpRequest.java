package com.example.beautyboutique.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data

public class SignUpRequest {
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("username")
    @NotBlank(message = "username is required")
    private String username;
    private String email;
    private String address;
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    @JsonProperty("role_id")
    private Integer roleId;

}
