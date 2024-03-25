package com.example.beautyboutique.DTOs.Requests.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String address;
    private Date dateOfBirth;;
    private String email;
    private String[] imageIds;
    private String[] imageUrls;
}
