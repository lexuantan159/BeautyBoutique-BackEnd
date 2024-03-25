package com.example.beautyboutique.DTOs.Requests.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String address;

    private String fullName;
    private Date dateOfBirth;
    @Email(message = "Email không đúng định dạng")
    private String email;
    private String[] imageIds;
    private String[] imageUrls;
}
